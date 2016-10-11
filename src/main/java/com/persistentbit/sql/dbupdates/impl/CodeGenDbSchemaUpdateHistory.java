package com.persistentbit.sql.dbupdates.impl;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.codegen.DbJavaGen;
import com.persistentbit.substema.compiler.SubstemaCompiler;
import com.persistentbit.substema.dependencies.DependencySupplier;
import com.persistentbit.substema.dependencies.SupplierDef;
import com.persistentbit.substema.dependencies.SupplierType;
import com.persistentbit.substema.javagen.GeneratedJava;
import com.persistentbit.substema.javagen.JavaGenOptions;

/**
 * Code generator for schema update db code
 *
 * @author Peter Muys
 * @since 11/10/2016
 */
public class CodeGenDbSchemaUpdateHistory {
    static public void main(String...args) throws Exception{
        SubstemaCompiler compiler = new SubstemaCompiler(new DependencySupplier(PList.val(new SupplierDef(SupplierType.resource,"/"))));
        JavaGenOptions options = new JavaGenOptions(true,true);
        PList<GeneratedJava> genList =DbJavaGen.generate(options,"com.persistentbit.sql.dbupdates.db",compiler);
    }
}
