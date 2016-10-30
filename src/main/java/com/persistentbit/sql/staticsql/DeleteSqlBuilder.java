package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.utils.ToDo;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.staticsql.expr.ETypeObject;

import java.util.Optional;

/**
 * TODO: Add comment
 *
 * @author Peter Muys
 * @since 13/10/2016
 */
public class DeleteSqlBuilder{

	private final DbType                       dbType;
	private final Delete                       delete;
	private final PMap<ETypeObject, TableInst> tables;

	public DeleteSqlBuilder(DbType dbType, Delete delete) {
		this.dbType = dbType;
		this.delete = delete;
		PMap<ETypeObject, TableInst> allUsed = PMap.empty();
		allUsed.put(delete.getTable(), new TableInst(delete.getTable().getInstanceName(), delete.getTable()));
		tables = allUsed;
	}

	private Optional<String> getTableInstance(ETypeObject obj) {
		return tables.getOpt(obj).map(ti -> ti.getName());
	}

	public String generate() {
		throw new ToDo();
		/*String nl = "\r\n";
        String res = "DELETE FROM  " + delete.getTable()._getTableName() + nl;
        if(delete.getWhere() != null){
            res += " WHERE " + ExprToSql.toSql(delete.getWhere(),this::getTableInstance,dbType);
        }

        return res;*/
	}

}
