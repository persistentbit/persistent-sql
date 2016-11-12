package com.persistentbit.sql.dbbuilder.impl;


import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.dbbuilder.DbBuilder;

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
	public void buildOrUpdate() {
		for(DbBuilder b : builders) {
			b.buildOrUpdate();
		}
	}

	@Override
	public boolean dropAll() {
		boolean ok = true;
		for(DbBuilder b : builders) {
			ok = ok && b.dropAll();
		}
		return ok;
	}

	@Override
	public boolean hasUpdatesThatAreDone() {
		for(DbBuilder b : builders) {
			if(b.hasUpdatesThatAreDone()) {
				return true;
			}
		}
		return false;
	}
}
