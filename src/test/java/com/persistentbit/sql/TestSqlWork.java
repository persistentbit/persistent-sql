package com.persistentbit.sql;

import com.persistentbit.core.OK;
import com.persistentbit.core.result.Result;
import com.persistentbit.core.testing.TestCase;
import com.persistentbit.core.testing.TestRunner;
import com.persistentbit.sql.sqlwork.SqlWork;
import com.persistentbit.sql.sqlwork.SqlWorkRunner;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * TODOC
 *
 * @author petermuys
 * @since 12/01/17
 */
public class TestSqlWork{

	static final SqlWork<OK>      createDbUpdateTestWork = SqlWork.function("Create Test Table").code(l -> tm -> {
		try(Statement stat = tm.get().createStatement()) {
			stat.executeUpdate("CREATE TABLE db_update_test (\n" +
								   "  id   INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),\n" +
								   "  name VARCHAR(256)\n" +
								   ")");
		}
		return OK.result;
	});
	static final SqlWork<Integer> select1234Work         = SqlWork.function("Select 1234").code(l -> tm -> {
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
		SqlWork<Integer> work1 = createDbUpdateTestWork.flatMap(ok -> select1234Work);
		Result<Integer>  res   = SqlWorkRunner.run(SQLTestTools.testDbConnector, work1);
		tr.add(res);
		tr.isEquals(res.orElseThrow(), 1234);

	});

	public void testAll() {
		TestRunner.runAndPrint(ModuleSql.logPrint, TestSqlWork.class);
	}

	public static void main(String[] args) {
		new TestSqlWork().testAll();
	}
}
