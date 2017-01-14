package com.persistentbit.sql.old;

/**
 * Test Substema code gen from database definition.
 *
 * @author petermuys
 * @since 1/11/16
 */
public class DbSubstemaGenTest extends AbstractTestWithTransactions{
/*
	@Test
	public void testCodeGen() {
		SubstemaCompiler compiler     = new SubstemaCompiler(new DependencySupplier().withResources());
		RSubstema        baseSubstema = compiler.compile("com.persistentbit.sql.test").orElseThrow();
		DbSubstemaGen    gen          = new DbSubstemaGen(dbConnector, baseSubstema, compiler);
		gen.loadTables();
		//gen.mergeWithBase();

		gen.mergeEmbedded(".*", ".*", "com.persistentbit.sql.test", "Address");
		baseSubstema = gen.replaceBase(true);
		SubstemaSourceGenerator codeGen = new SubstemaSourceGenerator();
		codeGen.addSubstema(baseSubstema);
		System.out.println(codeGen);

	}
	*/

}
