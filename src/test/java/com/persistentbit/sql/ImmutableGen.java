package com.persistentbit.sql;

import com.persistentbit.core.codegen.CaseClaseCodeBuilder;
import com.persistentbit.core.runners.CodeGen;

/**
 * User: petermuys
 * Date: 17/07/16
 * Time: 11:25
 */
public class ImmutableGen {
    static public void main(String...args){
        CaseClaseCodeBuilder.build(CaseClaseCodeBuilder.findTestSourcePath(ImmutableGen.class,"persistentbit-sql.marker.txt"));
    }
}
