package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.staticsql.expr.*;

import java.util.Optional;

/**
 * Created by petermuys on 1/10/16.
 */
public class Query{


	private final ETypeObject    from;
	final         boolean        distinct;
	final         PList<OrderBy> orderBy;
	private final PList<Join>    joins;
	private final ETypeBoolean   where;
	private final Tuple2<Long,Long>	limitAndOffset;

	public Query(ETypeObject from, boolean distinct,
				 PList<OrderBy> orderBy,
				 PList<Join> joins,
				 ETypeBoolean where,
				 Tuple2<Long,Long> limitAndOffset
	) {
		this.from = from;
		this.distinct = distinct;
		this.orderBy = orderBy;
		this.joins = joins;
		this.where = where;
		this.limitAndOffset = limitAndOffset;
	}

	public Query(ETypeObject from, PList<Join> joins) {
		this(from, false, PList.empty(), joins, null,null);
	}

	static public Query from(ETypeObject table) {
		return new Query(table, PList.empty());
	}


	public Query distinct() {
		return new Query(from, true, orderBy, joins, where,limitAndOffset);
	}


	public Query orderByDesc(Expr<?> expr) {
		return orderBy(new OrderBy(expr, OrderBy.Direction.desc));
	}

	public Query orderBy(OrderBy orderBy) {
		return new Query(from, distinct, this.orderBy.plus(orderBy), joins, where,limitAndOffset);
	}

	public Query orderByAsc(Expr<?> expr) {
		return orderBy(new OrderBy(expr, OrderBy.Direction.asc));
	}

	public Join leftJoin(ETypeObject table) {
		return new Join(this, Join.Type.left, table);
	}


	public Join rightJoin(ETypeObject table) {
		return new Join(this, Join.Type.right, table);
	}

	public Join innerJoin(ETypeObject table) {
		return new Join(this, Join.Type.inner, table);
	}

	public Join fullJoin(ETypeObject table) {
		return new Join(this, Join.Type.full, table);
	}

	public Query where(ETypeBoolean whereExpr) {
		return new Query(from, distinct, orderBy, joins, whereExpr,limitAndOffset);
	}

	Query addJoin(Join j) {
		return new Query(from, distinct, orderBy, joins.plus(j), where,limitAndOffset);
	}


	public ETypeObject getFrom() {
		return from;
	}

	public PList<Join> getJoins() {
		return joins;
	}

	public Optional<ETypeBoolean> getWhere() {
		return Optional.ofNullable(where);
	}


	public Query	limit(long limit){
		return new Query(from,distinct,orderBy,joins,where,Tuple2.of(limit,null));
	}
	public Query	limitAndOffset(long limit, long offset){
		return new Query(from,distinct,orderBy,joins,where,Tuple2.of(limit,offset));
	}

	public Optional<Long> getLimit() {
		if(limitAndOffset == null){
			return Optional.empty();
		}
		return limitAndOffset.get1();
	}
	public Optional<Long> getOffset() {
		if(limitAndOffset == null){
			return Optional.empty();
		}
		return limitAndOffset.get2();
	}

	public <T> Selection1<T> selection(Expr<T> selection) {
		return new Selection1<T>(this, selection);
	}

	public <T1, T2> Selection2<T1, T2> selection(
		Expr<T1> col1, Expr<T2> col2
	) {
		return new Selection2<>(this, col1, col2);
	}

	public <T1, T2, T3> Selection3<T1, T2, T3> selection(
		Expr<T1> col1, Expr<T2> col2, Expr<T3> col3
	) {
		return new Selection3<>(this, col1, col2, col3);
	}

	public <T1, T2, T3, T4> Selection4<T1, T2, T3, T4> selection(
		Expr<T1> col1, Expr<T2> col2, Expr<T3> col3, Expr<T4> col4
	) {
		return new Selection4<>(this, col1, col2, col3, col4);
	}

	public <T1, T2, T3, T4, T5> Selection5<T1, T2, T3, T4, T5> selection(
		Expr<T1> col1, Expr<T2> col2, Expr<T3> col3, Expr<T4> col4, Expr<T5> col5
	) {
		return new Selection5<>(this, col1, col2, col3, col4, col5);
	}

	public <T1, T2, T3, T4, T5, T6> Selection6<T1, T2, T3, T4, T5, T6> selection(
		Expr<T1> col1, Expr<T2> col2, Expr<T3> col3, Expr<T4> col4, Expr<T5> col5, Expr<T6> col6
	) {
		return new Selection6<>(this, col1, col2, col3, col4, col5, col6);
	}

	public <T1, T2, T3, T4, T5, T6, T7> Selection7<T1, T2, T3, T4, T5, T6, T7> selection(
		Expr<T1> col1, Expr<T2> col2, Expr<T3> col3, Expr<T4> col4, Expr<T5> col5, Expr<T6> col6, Expr<T7> col7
	) {
		return new Selection7<>(this, col1, col2, col3, col4, col5, col6, col7);
	}

}
