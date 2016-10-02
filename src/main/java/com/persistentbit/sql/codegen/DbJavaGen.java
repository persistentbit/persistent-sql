package com.persistentbit.sql.codegen;


import com.persistentbit.core.Nullable;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PSet;
import com.persistentbit.core.sourcegen.SourceGen;
import com.persistentbit.core.tokenizer.Token;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.utils.StringUtils;
import com.persistentbit.core.utils.builders.NOT;
import com.persistentbit.core.utils.builders.SET;
import com.persistentbit.sql.staticsql.expr.*;
import com.persistentbit.substema.compiler.*;
import com.persistentbit.substema.javagen.GeneratedJava;
import com.persistentbit.substema.javagen.JavaGenOptions;
import com.persistentbit.substema.compiler.values.*;
import com.persistentbit.substema.javagen.JavaGenUtils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by petermuys on 14/09/16.
 */
public class DbJavaGen {

    private final JavaGenOptions options;
    private final RSubstema service;
    private final String  packageName;
    private PList<GeneratedJava>    generatedJava = PList.empty();
    private SubstemaCompiler    compiler;
    private PSet<RClass> allExternalValueClasses;


    private DbJavaGen(JavaGenOptions options, RSubstema service, SubstemaCompiler compiler) {
        this.options = options;
        this.service = service;
        this.packageName = service.getPackageName();
        this.compiler = compiler;
        this.allExternalValueClasses = findAllExternalValueClasses();
    }

    static public PList<GeneratedJava>  generate(JavaGenOptions options,RSubstema service,SubstemaCompiler compiler){
        return new DbJavaGen(options,service,compiler).generateService();
    }

    public PList<GeneratedJava> generateService(){
        PList<GeneratedJava> result = PList.empty();

        //System.out.println("ALL EXTERNAL: " + allExternalValueClasses);
        result = result.plusAll(service.getValueClasses().map(vc -> new Generator().generateValueClass(vc)));
        result = result.plusAll(allExternalValueClasses.map(c -> {
            RSubstema substema = compiler.compile(c.getPackageName());
            RValueClass vc = substema.getValueClasses().find(evc -> evc.getTypeSig().getName().equals(c)).get();
            return new Generator().generateValueClass(vc);
        }));
        result = result.plus(new Generator().generateDb(service.getValueClasses()));
        return result.filterNulls().plist();
    }

    private PSet<RClass> findAllExternalValueClasses() {
        PSet<RClass> found = PSet.empty();
        for(RValueClass vc : service.getValueClasses()){
            found = findExternalValueClasses(found,vc.getTypeSig().getName());
        }
        return found;
    }

    private Optional<RValueClass> getValueClass(RClass cls){
        return service.getValueClasses().find(vc -> vc.getTypeSig().getName().equals(cls));
    }

    private PSet<RClass> findExternalValueClasses(PSet<RClass> found,RClass cls){
        if(found.contains(cls)){
            return found;
        }
        if(SubstemaUtils.isSubstemaClass(cls)){
            return found;
        }
        RValueClass vc = getValueClass(cls).orElse(null);


        if(cls.getPackageName().equals(packageName) == false){
            found = found.plus(cls);
        }

        if(vc == null){
            return found;
        }

        for(RProperty prop : vc.getProperties()){

            found = found.plusAll(findExternalValueClasses(found,prop.getValueType().getTypeSig().getName()));
        }
        return found;
    }

    private class Generator extends SourceGen{
        private PSet<RClass>    imports = PSet.empty();
        private SourceGen       header = new SourceGen();


        public Generator() {
            header.println("// GENERATED CODE FOR DB TABLE: DO NOT CHANGE!");
            header.println("");
        }

        public GeneratedJava    toGenJava(RClass cls){
            SourceGen sg = new SourceGen();
            header.println("package " + packageName + ";");
            header.println("");
            sg.add(header);
            imports.filter(i -> i.getPackageName().equals(packageName) == false).forEach(i -> {
                sg.println("import " + i.getPackageName() + "." + i.getClassName() + ";");
            });
            sg.println("");
            sg.add(this);
            return new GeneratedJava(cls,sg.writeToString());
        }


        private void addImport(RClass cls){

            imports = imports.plus(cls);
        }
        private void addImport(Class<?> cls){
            addImport(new RClass(cls.getPackage().getName(),cls.getSimpleName()));
        }

        private RClass toExprClass(RClass cls){
            return cls.withClassName(cls.getClassName()+"_").withPackageName(packageName);
        }

        public GeneratedJava    generateValueClass(RValueClass vc){
            RClass vcCls = vc.getTypeSig().getName();
            RClass cls = toExprClass(vcCls);


            addImport(vcCls);
            addImport(Expr.class);
            addImport(ETypeObject.class);
            bs("public class " + cls.getClassName() + " implements ETypeObject<" + vcCls.getClassName() + ">");
            {
                println("private final Expr __parent;");

                println("");
                bs("public " + cls.getClassName() + "(Expr parent)");{
                    println("this.__parent = parent;");
                }be();
                println("");
                println("@Override");
                println("public Expr getParent() { return this.__parent; }");

                println("");
                println("@Override");
                println("public String toString() { return getInstanceName(); }");

                println("");
                vc.getProperties().forEach(this::generateProperty);

                // ****************** _all()
                println("");
                println("@Override");
                addImport(PList.class);
                addImport(Tuple2.class);
                bs("public PList<Tuple2<String,Expr>> _all()");{
                    if(vc.getProperties().isEmpty()){
                        println("return PList.empty();");
                    } else {
                        println("return PList.val(" + vc.getProperties().map(p -> "Tuple2.of(\"" + p.getName()  + "\","  +p.getName() +")").toString(", ") + ");");
                    }
                }be();
                // ***************** asValues
                println("");
                bs("static public PList<Expr> asValues(Object obj)");{
                    println(vcCls.getClassName() + " v = (" + vcCls.getClassName() + ")obj;");
                    println("PList<Expr> r = PList.empty();");
                    vc.getProperties().forEach(p -> {
                                println(generateValueExpr(p));
                            });
                    println("return r;");
                }be();

            }be();
            return toGenJava(cls);
        }

        public String generateValueExpr(RProperty p){
            String type;

            RClass cls = p.getValueType().getTypeSig().getName();
            String getter = "v.get" + StringUtils.firstUpperCase(p.getName()) + "()";
            if(p.getValueType().isRequired() == false){
                getter = getter + ".orElse(null)";
            }
            if(SubstemaUtils.isNumberClass(cls)
                    || cls.equals(SubstemaUtils.booleanRClass)
                    || cls.equals(SubstemaUtils.stringRClass)
                    ){
                return "r = r.plus(Expr.val(" + getter + "));";
            } else {
                RClass nc = toExprClass(cls);
                addImport(nc);
                return "r = r.plusAll(" + JavaGenUtils.toString(packageName,nc) + ".asValues(" + getter + "));";
            }
        }

        public GeneratedJava generateDb(PList<RValueClass> valueClasses) {
            /*
            public class Db implements ExprDb<Db> {
    public Amount_ amount() {
        return new Amount_(this);
    }
}
             */
            RClass dbCls = new RClass(packageName,"Db");
            addImport(dbCls);
            addImport(ExprDb.class);
            bs("public class Db implements ExprDb<Db>");{
                valueClasses.forEach(vc -> {
                    RClass cls = vc.getTypeSig().getName();
                    RClass ecls = toExprClass(cls);
                    addImport(ecls);
                    println("public " + JavaGenUtils.toString(packageName,ecls) + " " + StringUtils.firstLowerCase(cls.getClassName()) + "(){ return new " + JavaGenUtils.toString(packageName,ecls) +"(this); }");
                });

            }be();
            return toGenJava(dbCls);
        }

        private void generateProperty(RProperty property){

            String type;
            String value;
            RClass cls = property.getValueType().getTypeSig().getName();
            if(SubstemaUtils.isNumberClass(cls)){
                addImport(ETypeNumber.class);
                addImport(ExprPropertyNumber.class);
                type = "ETypeNumber<" + cls.getClassName() + ">";
                value = "new ExprPropertyNumber<>(this,\"" + property.getName() + "\");";
            } else if (cls.equals(SubstemaUtils.booleanRClass)){
                addImport(ETypeBoolean.class);
                addImport(ExprPropertyBoolean.class);
                type = "ETypeBoolean";
                value = "new ExprPropertyBoolean(this,\"" + property.getName() + "\");";
            } else if (cls.equals(SubstemaUtils.stringRClass)){
                addImport(ETypeString.class);
                addImport(ExprPropertyString.class);
                type = "ETypeString";
                value = "new ExprPropertyString(this,\"" + property.getName() + "\");";
            } else {
                RClass nc = toExprClass(cls);
                //addImport(cls);
                addImport(nc);
                addImport(ExprProperty.class);
                type = JavaGenUtils.toString(packageName,nc);
                value = "new " + type + "(new ExprProperty(this,\"" + property.getName() + "\"));";

            }
            println("public " + type +" " + property.getName() + " = " + value);
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


}
