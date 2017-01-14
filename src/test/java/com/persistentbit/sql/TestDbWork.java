package com.persistentbit.sql;

import com.persistentbit.core.testing.TestCase;
import com.persistentbit.core.testing.TestRunner;

/**
 * TODOC
 *
 * @author petermuys
 * @since 14/01/17
 */
public class TestDbWork extends SQLTestTools{

	static final TestCase testDbBuilder = TestCase.name("DbBuilder").code(tr -> {
		tr.isFalse(run(builder.hasUpdatesThatAreDone()).orElseThrow());
		tr.isSuccess(run(builder.buildOrUpdate()));
		tr.isTrue(run(builder.hasUpdatesThatAreDone()).orElseThrow());
		tr.isSuccess(run(builder.dropAll()));

		tr.isFalse(run(builder.hasUpdatesThatAreDone()).orElseThrow());
		tr.isSuccess(run(builder.buildOrUpdate()));
		tr.isTrue(run(builder.hasUpdatesThatAreDone()).orElseThrow());
		tr.isSuccess(run(builder.dropAll()));
	});


	public void testAll() {
		TestRunner.runAndPrint(ModuleSql.createLogPrinter(true), TestDbWork.class);
	}

	public static void main(String[] args) {
		ModuleSql.createLogPrinter(true).registerAsGlobalHandler();
		new TestDbWork().testAll();
	}
}
