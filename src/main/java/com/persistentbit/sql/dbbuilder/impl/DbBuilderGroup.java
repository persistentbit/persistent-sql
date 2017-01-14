package com.persistentbit.sql.dbbuilder.impl;


import com.persistentbit.core.OK;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.core.result.Result;
import com.persistentbit.sql.dbbuilder.DbBuilder;
import com.persistentbit.sql.staticsql.SSqlWork;

/**
 * @author Peter Muys
 * @since 18/06/16
 */
public class DbBuilderGroup implements DbBuilder{

	private final PList<DbBuilder> builders;

	public DbBuilderGroup(DbBuilder... builders) {
		this.builders = PStream.from(builders).plist();
	}


	@Override
	public SSqlWork<OK> buildOrUpdate() {
		return SSqlWork.sequence(builders.map(DbBuilder::buildOrUpdate));
	}

	@Override
	public SSqlWork<OK> dropAll() {
		return SSqlWork.sequence(builders.map(DbBuilder::buildOrUpdate));
	}

	@Override
	public SSqlWork<Boolean> hasUpdatesThatAreDone() {
		return SSqlWork.function().code(log -> (dbc, tm) -> {
			boolean ok = true;
			for(DbBuilder b : builders) {
				Result<Boolean> itemOk = b.hasUpdatesThatAreDone().execute(dbc, tm);
				if(itemOk.isError()) {
					return itemOk;
				}
				ok = ok && itemOk.orElseThrow();
				log.add(itemOk);
			}
			return Result.success(ok);
		});
	}
}
