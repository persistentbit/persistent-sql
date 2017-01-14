package com.persistentbit.sql.experiments;

import com.persistentbit.core.OK;
import com.persistentbit.core.result.Result;
import com.persistentbit.core.testing.TestCase;
import com.persistentbit.core.testing.TestRunner;
import com.persistentbit.sql.InMemConnectionSupplier;
import com.persistentbit.sql.ModuleSql;
import com.persistentbit.sql.dbwork.DbWork;
import com.persistentbit.sql.dbwork.DbWorkRunner;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * TODOC
 *
 * @author petermuys
 * @since 12/01/17
 */
public class TestDbWork{

	static final DbWork<OK>      createDbUpdateTestWork = tm -> Result.function("Create Test Table").code(l -> {
		try(Statement stat = tm.get().createStatement()) {
			stat.executeUpdate("CREATE TABLE db_update_test (\n" +
								   "  id   INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),\n" +
								   "  name VARCHAR(256)\n" +
								   ")");
		}
		return OK.result;
	});
	static final DbWork<Integer> select1234Work         = tm -> Result.function("Select 1234").code(l -> {
		try(Statement stat = tm.get().createStatement()) {
			stat.executeUpdate("insert into db_update_test (name) values('Peter Muys')");
		}
		try(PreparedStatement stat = tm.get().prepareStatement("select 1234 from db_update_test")) {
			try(ResultSet rs = stat.executeQuery()) {
				rs.next();
				return Result.success(rs.getInt(1));
			}
		}
	});

	static final TestCase testWork = TestCase.name("First Test").code(tr -> {
		DbWork<Integer> work1 = createDbUpdateTestWork.flatMap(ok -> select1234Work);


		InMemConnectionSupplier cs = new InMemConnectionSupplier();

		//DbTransManagerImpl tm  = new DbTransManagerImpl(cs);
		//Result<Integer>    res = tm.run(work1);
		Result<Integer> res = DbWorkRunner.run(cs, work1);
		tr.add(res);
		tr.isEquals(res.orElseThrow(), 1234);

	});

	public void testAll() {
		TestRunner.runAndPrint(ModuleSql.createLogPrinter(true), TestDbWork.class);
	}

	public static void main(String[] args) {
		new TestDbWork().testAll();
	}
}
