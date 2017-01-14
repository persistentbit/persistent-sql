package com.persistentbit.sql.old;

/**
 * Create Substema Source code for the test db
 *
 * @author petermuys
 * @since 2/11/16
 */
public class RunnerCreateDbSustema{
/*
	public static void main(String[] args) {
		InMemConnectionSupplier dbConnector = new InMemConnectionSupplier();

		TransactionRunnerPerThread trans   = new TransactionRunnerPerThread(dbConnector);
		TestDbBuilderImpl          builder = new TestDbBuilderImpl(new DbDerby(), null, trans);

		if(builder.hasUpdatesThatAreDone()) {
			builder.dropAll();
		}
		builder.buildOrUpdate();


		SubstemaCompiler compiler     = new SubstemaCompiler(new DependencySupplier().withResources());
		RSubstema        baseSubstema = compiler.compile("com.persistentbit.sql.test").orElseThrow();
		DbSubstemaGen    gen          = new DbSubstemaGen(dbConnector, baseSubstema, compiler);
		gen.loadTables();

		gen.mergeWithBase();

		gen.mergeEmbedded(".*", ".*", "com.persistentbit.sql.test", "Address");

		SubstemaSourceGenerator codeGen = new SubstemaSourceGenerator();
		gen.getValueClasses().forEach(vc -> {
			codeGen.addValueClass(vc);
		});
		System.out.println(codeGen);
	}*/
}
