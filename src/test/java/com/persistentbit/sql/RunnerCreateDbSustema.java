package com.persistentbit.sql;

import com.persistentbit.sql.databases.DbDerby;
import com.persistentbit.sql.substemagen.DbSubstemaGen;
import com.persistentbit.sql.transactions.TransactionRunnerPerThread;
import com.persistentbit.substema.compiler.SubstemaCompiler;
import com.persistentbit.substema.compiler.values.RSubstema;
import com.persistentbit.substema.dependencies.DependencySupplier;
import com.persistentbit.substema.substemagen.SubstemaSourceGenerator;

/**
 * Create Substema Source code for the test db
 *
 * @author petermuys
 * @since 2/11/16
 */
public class RunnerCreateDbSustema{

	public static void main(String[] args) {
		InMemConnectionProvider dbConnector = new InMemConnectionProvider();

		TransactionRunnerPerThread trans   = new TransactionRunnerPerThread(dbConnector);
		TestDbBuilderImpl          builder = new TestDbBuilderImpl(new DbDerby(), null, trans);

		if(builder.hasUpdatesThatAreDone()) {
			builder.dropAll();
		}
		builder.buildOrUpdate();


		SubstemaCompiler compiler     = new SubstemaCompiler(new DependencySupplier().withResources());
		RSubstema        baseSubstema = compiler.compile("com.persistentbit.sql.test");
		DbSubstemaGen    gen          = new DbSubstemaGen(dbConnector, baseSubstema, compiler, null, null);
		gen.loadTables();

		gen.mergeWithBase();

		gen.mergeEmbedded(".*", ".*", "com.persistentbit.sql.test", "Address");

		SubstemaSourceGenerator codeGen = new SubstemaSourceGenerator();
		gen.getValueClasses().forEach(vc -> {
			codeGen.addValueClass(vc);
		});
		System.out.println(codeGen);
	}
}
