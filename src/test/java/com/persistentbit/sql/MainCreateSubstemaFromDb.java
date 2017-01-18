package com.persistentbit.sql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.sourcegen.SourcePath;
import com.persistentbit.core.utils.IO;
import com.persistentbit.sql.staticsql.codegen.DbJavaGen;
import com.persistentbit.sql.substemagen.DbSubstemaGen;
import com.persistentbit.substema.compiler.SubstemaCompiler;
import com.persistentbit.substema.compiler.values.RSubstema;
import com.persistentbit.substema.dependencies.DependencySupplier;
import com.persistentbit.substema.javagen.GeneratedJava;
import com.persistentbit.substema.javagen.JavaGenOptions;
import com.persistentbit.substema.substemagen.SubstemaSourceGenerator;

import java.nio.file.Path;

/**
 * TODOC
 *
 * @author petermuys
 * @since 15/01/17
 */
public class MainCreateSubstemaFromDb extends SQLTestTools{

	public static void main(String[] args) {
		logPrint.registerAsGlobalHandler();
		dbRun.run(builder.buildOrUpdate()).orElseThrow();


		SubstemaCompiler compiler     = new SubstemaCompiler(
			new DependencySupplier().withResources()
		);
		RSubstema        baseSubstema = compiler.compile("com.persistentbit.sql.test").orElseThrow();
		DbSubstemaGen    gen          = new DbSubstemaGen(testDbConnector, baseSubstema, compiler);
		gen.loadTables().orElseThrow();

		//gen.mergeWithBase();

		gen.mergeEmbedded(".*", ".*", "com.persistentbit.sql.test", "Address");

		baseSubstema = gen.replaceBase(true);
		SubstemaSourceGenerator codeGen = new SubstemaSourceGenerator();
		codeGen.addSubstema(baseSubstema);
		String packageName = "com.persistentbit.sql.test";
		Path source =SourcePath.findTestSourcePath(MainCreateSubstemaFromDb.class, packageName+".substema").orElseThrow();
		Path resources = SourcePath.findTestResourcePath(MainCreateSubstemaFromDb.class,packageName+".substema").orElseThrow();


		IO.writeFile(codeGen.toString(),resources.resolve(packageName+".substema").toFile(),IO.utf8);
		System.out.println(codeGen);

		PList<GeneratedJava> generatedJavas = DbJavaGen
			.generate(new JavaGenOptions(true,true), "com.persistentbit.sql.test", compiler)
			.map(gr -> gr.orElseThrow());

		Path destSource = source.resolve("com").resolve("persistentbit").resolve("sql").resolve("test");
		IO.mkdirsIfNotExisting(source.toFile())
			.ifPresent(path -> {
				generatedJavas.forEach(gj -> {
					IO.writeFile(gj.code,destSource.resolve(gj.name.getClassName()+".java").toFile(),IO.utf8);
				});
			})
		;

	}
}
