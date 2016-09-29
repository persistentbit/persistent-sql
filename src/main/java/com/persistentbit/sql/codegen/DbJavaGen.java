package com.persistentbit.sql.codegen;


import com.persistentbit.core.Nullable;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PSet;
import com.persistentbit.core.sourcegen.SourceGen;
import com.persistentbit.core.tokenizer.Token;
import com.persistentbit.core.utils.builders.NOT;
import com.persistentbit.core.utils.builders.SET;
import com.persistentbit.sql.staticsql.ETypeObject;
import com.persistentbit.sql.staticsql.Expr;
import com.persistentbit.substema.javagen.GeneratedJava;
import com.persistentbit.substema.javagen.JavaGenOptions;
import com.persistentbit.substema.compiler.SubstemaParser;
import com.persistentbit.substema.compiler.SubstemaTokenType;
import com.persistentbit.substema.compiler.SubstemaTokenizer;
import com.persistentbit.substema.compiler.values.*;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by petermuys on 14/09/16.
 */
public class DbJavaGen {

    private final JavaGenOptions options;
    private final RSubstema service;
    private PList<GeneratedJava>    generatedJava = PList.empty();

    private final String servicePackageName;

    private DbJavaGen(JavaGenOptions options, String packageName, RSubstema service) {
        this.servicePackageName = packageName;
        this.options = options;
        this.service = service;
    }

    static public PList<GeneratedJava>  generate(JavaGenOptions options,String packageName,RSubstema service){
        return new DbJavaGen(options,packageName,service).generateService();
    }

    public PList<GeneratedJava> generateService(){
        //RServiceValidator.validate(service);
        PList<GeneratedJava> result = PList.empty();
        result = result.plusAll(service.getEnums().map(e -> new Generator().generateEnum(e)));
        result = result.plusAll(service.getValueClasses().map(vc -> new Generator().generateValueClass(vc)));

        return result.filterNulls().plist();
    }

    private class Generator extends SourceGen{
        private PSet<RClass>    imports = PSet.empty();
        private SourceGen       header = new SourceGen();
        private String          packageName;

        public Generator() {
            header.println("// GENERATED CODE FOR DB TABLE: DO NOT CHANGE!");
            header.println("");
        }

        public GeneratedJava    toGenJava(RClass cls){
            SourceGen sg = new SourceGen();
            header.println("package " + servicePackageName + ";");
            header.println("");
            sg.add(header);
            imports.filter(i -> i.getPackageName().equals(servicePackageName) == false).forEach(i -> {
                sg.println("import " + i.getPackageName() + "." + i.getClassName() + ";");
            });
            sg.println("");
            sg.add(this);
            return new GeneratedJava(cls,sg.writeToString());
        }

        public GeneratedJava    generateEnum(REnum e ){
            bs("public enum " + e.name.getClassName());{
                println(e.values.toString(","));
            }be();
            return toGenJava(e.name.withClassName(e.name.getClassName()+"_"));
        }
        private void addImport(RClass cls){
            imports = imports.plus(cls);
        }
        private void addImport(Class<?> cls){
            addImport(new RClass(cls.getPackage().getName(),cls.getSimpleName()));
        }

        private RClass toExprClass(RClass cls){
            return cls.withClassName(cls.getClassName()+"_");
        }

        public GeneratedJava    generateValueClass(RValueClass vc){
            RClass cls = toExprClass(vc.getTypeSig().getName());


            addImport(vc.getTypeSig().getName());
            addImport(Expr.class);
            addImport(ETypeObject.class);
            bs("public class " + cls.getClassName() + " implements ETypeObject<" + vc.getTypeSig().getName().getClassName() + ">");
            {
                println("private final Expr __parent;");

                bs("public " + cls.getClassName() + "(Expr parent)");{
                    println("this.__parent = parent;");
                }be();

                vc.getProperties().forEach(p -> {

                    //println(toString(p.getValueType(), true) + " " + p.getName() + ";");
                });
            }be();
            return toGenJava(cls);
        }

        private PList<RProperty>   getRequiredProps(RValueClass vc) {
            return vc.getProperties().filter(p->p.getDefaultValue().isPresent() == false && p.getValueType().isRequired());
        }

        private String getBuilderGenerics(RValueClass vc){
            return getBuilderGenerics(vc,PMap.empty());
        }
        private String getBuilderGenerics(RValueClass vc, PMap<String,String> namesReplace){
            PList<String> requiredProperties = getRequiredProps(vc).zipWithIndex().map(t -> namesReplace.getOpt(t._2.getName()).orElse("_T" + (t._1+1))).plist();
            if(requiredProperties.isEmpty()==false){
                addImport(SET.class);
                addImport(NOT.class);
            }
            requiredProperties = requiredProperties.plusAll(vc.getTypeSig().getGenerics().map(g -> g.getName().getClassName()));
            if(requiredProperties.isEmpty()){
                return "";
            }
            return requiredProperties.toString("<",",",">");
        }

        private boolean isRequired(RProperty p){
            return p.getDefaultValue().isPresent() == false && p.getValueType().isRequired();
        }

        private String toString(RTypeSig sig){
            return toString(sig,false);
        }
        private String toPrimString(RTypeSig sig){
            return toString(sig,true);
        }


        private String toString(RTypeSig sig,boolean asPrimitive){
            String gen = sig.getGenerics().isEmpty() ? "" : sig.getGenerics().map(g -> toString(g)).toString("<",",",">");
            String pname = sig.getName().getPackageName();
            String name = sig.getName().getClassName();

            switch(name){
                case "List": name = "PList"; addImport(PList.class); break;
                case "Set": name = "PSet"; addImport(PSet.class); break;
                case "Map": name= "PMap"; addImport(PMap.class); break;

                case "Boolean": name = asPrimitive ? "boolean" : name; break;
                case "Byte": name = asPrimitive ? "byte" : name; break;
                case "Short": name = asPrimitive ? "short" : name; break;
                case "Integer": name = asPrimitive ? "int" : name; break;
                case "Long": name = asPrimitive ? "long" : name; break;
                case "Float": name = asPrimitive ? "float" : name; break;
                case "Double": name = asPrimitive ? "double" : name; break;

                case "String": break;

                default:
                    addImport(new RClass(pname,name));
                    break;
            }

            return name + gen;
        }

        private boolean isPrimitive(RTypeSig sig){
            return toString(sig,true).equals(toString(sig,false)) == false;
        }

        private String firstUpper(String s){
            return s.substring(0,1).toUpperCase() + s.substring(1);
        }

        private String toString(RValueType vt,boolean isFinal){
            String res = "";
            String value = vt.isRequired() ? toPrimString(vt.getTypeSig()) : toString(vt.getTypeSig());
            if(vt.isRequired() == false){
                addImport(Nullable.class);

                if(options.generateGetters == false){
                    addImport(Optional.class);
                    value = "Optional<" + value + ">";
                } else {
                    res += "@Nullable ";
                }
            }
            String access =  options.generateGetters ? "private" : "public";
            access += isFinal ?  " final " : " ";
            return res + access  + value;

        }


    }




    static public void main(String...args) throws Exception{
        String rodFileName= "com.persistentbit.parser.substema";
        URL url = DbJavaGen.class.getResource("/" + rodFileName);
        System.out.println("URL: " + url);
        Path path = Paths.get(url.toURI());
        System.out.println("Path  = " + path);
        String rod = new String(Files.readAllBytes(path));
        SubstemaTokenizer tokenizer = new SubstemaTokenizer();
        PList<Token<SubstemaTokenType>> tokens = tokenizer.tokenize(rodFileName,rod);
        String packageName  = "com.persistentbit.test";
        SubstemaParser parser = new SubstemaParser(packageName,tokens);
        RSubstema service = parser.parseSubstema();
        System.out.println(service);
        PList<GeneratedJava> gen = DbJavaGen.generate(new JavaGenOptions(),packageName,service);
        gen.forEach(gj -> {
            System.out.println(gj.code);
            System.out.println("-----------------------------------");
        });
    }
}
