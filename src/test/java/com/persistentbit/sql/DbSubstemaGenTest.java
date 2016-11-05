package com.persistentbit.sql;

import com.persistentbit.sql.substemagen.DbSubstemaGen;
import com.persistentbit.substema.compiler.SubstemaCompiler;
import com.persistentbit.substema.compiler.values.RSubstema;
import com.persistentbit.substema.dependencies.DependencySupplier;
import com.persistentbit.substema.substemagen.SubstemaSourceGenerator;
import org.junit.Test;

/**
 * Test Substema code gen from database definition.
 *
 * @author petermuys
 * @since 1/11/16
 */
public class DbSubstemaGenTest extends AbstractTestWithTransactions{

	@Test
	public void testCodeGen() {
		SubstemaCompiler compiler     = new SubstemaCompiler(new DependencySupplier().withResources());
		RSubstema        baseSubstema = compiler.compile("com.persistentbit.sql.test");
		DbSubstemaGen    gen          = new DbSubstemaGen(dbConnector, baseSubstema, compiler, null, null);
		gen.loadTables();

		//gen.mergeWithBase();

		gen.mergeEmbedded("com.persistentbit.sql.test", "Address");
		baseSubstema = gen.replaceBase(true);
		SubstemaSourceGenerator codeGen = new SubstemaSourceGenerator();
		codeGen.addSubstema(baseSubstema);
		System.out.println(codeGen);

	}

}
