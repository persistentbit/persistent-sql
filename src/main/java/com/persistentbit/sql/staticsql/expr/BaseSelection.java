package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.exceptions.ToDo;
import com.persistentbit.core.result.Result;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.sqlwork.DbTransManager;
import com.persistentbit.sql.staticsql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Peter Muys
 * @since 14/10/16
 */
public abstract class BaseSelection<T> implements ETypeSelection<T> {

    private final Query   query;
    private final Expr<T> selection;

    public BaseSelection(Query query, Expr<T> selection) {
        this.query = query;
        this.selection = selection;
    }

    @Override
    public ETypeObject<T> withNewParent(ETypePropertyParent newParent) {
        throw new ToDo();
    }

    public Query getQuery() {
        return query;
    }

    @Override
    public Result<PList<T>> execute(DbContext dbc, DbTransManager tm) throws Exception {
        return Result.function(dbc, tm).code(log -> {
            QuerySqlBuilder b = new QuerySqlBuilder(this, dbc);

            log.info(b.generateNoParams());

            Tuple2<String, Consumer<PreparedStatement>> generatedQuery = b.generate();
            try(PreparedStatement s = tm.get().prepareStatement(generatedQuery._1)) {
                generatedQuery._2.accept(s);
                ExprRowReader exprReader = new ExprRowReader();
                try(ResultSet rs = s.executeQuery()) {
                    ResultSetRowReader rowReader = new ResultSetRowReader(rs);
                    PList<T>           res       = PList.empty();
                    while(rs.next()) {
                        res = res.plus(read(rowReader, exprReader));
                        rowReader.nextRow();
                    }
                    return Result.success(res);
                }
            }
        });
    }



    public <U> BaseSelection<U> mapSelection(Function<T, U> recordMapper) {
        return new Selection1<>(query, new EMapper<>(selection, recordMapper));
    }

    public DbWork<T> justOne() {
        return DbWork.function().code(l -> (dbc, tm) ->
            this.execute(dbc, tm).flatMap(list -> Result.fromOpt(list.headOpt()))
        );
    }

    @Override
    public String _asParentName(ExprToSqlContext context, String propertyName) {
        return context.uniqueInstanceName(this, "Selection") + "." + propertyName;
    }

    @Override
    public PList<Expr<?>> _asExprValues(T value) {
        throw new ToDo();
    }

    @Override
    public String _toSql(ExprToSqlContext context) {
        QuerySqlBuilder b = new QuerySqlBuilder(this, context.getDbContext());
        return b.generate(context, true);
    }


    public class SelectionProperty<E> implements Expr<E> {

        private String propertyName;
        private Expr<E> expr;

        public SelectionProperty(String propertyName, Expr<E> expr) {
            this.propertyName = propertyName;
            this.expr = expr;
        }

        @Override
        public String toString() {
            return "Selection." + propertyName;
        }


        public ETypeObject getParent() {
            return BaseSelection.this;
        }

        public String getColumnName() {
            return propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }


        @Override
        public E read(RowReader _rowReader, ExprRowReaderCache _cache) {
            return expr.read(_rowReader, _cache);
        }

        @Override
        public String _toSql(ExprToSqlContext context) {
            return _asParentName(context, propertyName);

        }

        @Override
        public PList<Expr<?>> _expand() {
            return PList.val(expr);
        }

        public Expr<E> _getExpr() {
            return expr;
        }
    }
}
