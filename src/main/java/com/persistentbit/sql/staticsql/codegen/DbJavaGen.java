package com.persistentbit.sql.staticsql.codegen;


import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PSet;
import com.persistentbit.core.logging.PLog;
import com.persistentbit.core.sourcegen.SourceGen;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.utils.StringUtils;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.staticsql.DbSql;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;
import com.persistentbit.sql.staticsql.expr.*;
import com.persistentbit.sql.transactions.SQLTransactionRunner;
import com.persistentbit.substema.compiler.SubstemaCompiler;
import com.persistentbit.substema.compiler.SubstemaUtils;
import com.persistentbit.substema.compiler.values.*;
import com.persistentbit.substema.javagen.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Created by petermuys on 14/09/16.
 */
public class DbJavaGen {

    private final JavaGenOptions options;
    private final RSubstema substema;

    private final SubstemaCompiler    compiler;
    private PSet<RClass> allExternalValueClasses;


    static public final String packageDbAnnotations = "com.persistentbit.sql.annotations";
    static public final RClass rclassTable = new RClass(packageDbAnnotations,"Table");
    static public final RClass rclassColumn = new RClass(packageDbAnnotations,"Column");
    static public final RClass rclassAutoGen = new RClass(packageDbAnnotations,"AutoGen");
    static public final RClass rclassKey = new RClass(packageDbAnnotations,"Key");

    private DbJavaGen(JavaGenOptions options, String packageName, SubstemaCompiler compiler) {
        this.options = options;

        /*this.compiler = compiler
                .withImplicitImportPackages(
                        compiler.getImplicitImportPackages().plus(packageDbAnnotations)
                );*/
        this.compiler = compiler;
        this.substema = this.compiler.compile(packageName);

        this.allExternalValueClasses = findAllExternalDefinitions();
    }

    /**
     * Generate all java classes for this DB Substema.
     *
     * @param options   Code gen options
     * @param compiler  The substema compiler
     * @return  PList with all the generated java files
     */
    static public PList<GeneratedJava>  generate(JavaGenOptions options,String packageName,SubstemaCompiler compiler){
        return new DbJavaGen(options,packageName,compiler).generateService();
    }

    /**
     * Generate all java classes for this DB Substema.
     * @return PList with all the generated java files
     */
    public PList<GeneratedJava> generateService(){

        PList<GeneratedJava> tableValueClasses = SubstemaJavaGen.generate(compiler,options,substema);

        //Generate value classes defined in this substema
        PList<GeneratedJava> generatedForThisSubstema =
                substema.getValueClasses()
                        .map(vc -> new Generator(compiler,substema.getPackageName())
                                .generateValueClass(vc)
                        )
                ;


        //For all external case classes that are used
        //in this substema, generate the db description
        PList<GeneratedJava> generatedDescriptions =
                allExternalValueClasses.map( c-> {
                    if(c.getPackageName().isEmpty()){
                        //must be an enum;
                        return null;
                    }
                    RSubstema substema = compiler.compile(c.getPackageName());
                    RValueClass vc = substema.getValueClasses().find(evc -> evc.getTypeSig().getName().equals(c)).orElse(null);
                    if(vc == null){
                        return null;
                    }
                    return new Generator(compiler,this.substema.getPackageName()).generateValueClass(vc);
                }).filterNulls().plist();


        GeneratedJava generatedDbClass = new Generator(compiler,substema.getPackageName()).generateDb(substema.getValueClasses());


        return PList.val(generatedDbClass)
                .plusAll(generatedForThisSubstema)
                .plusAll(generatedDescriptions)
                .plusAll(tableValueClasses)
                ;
    }



    private class Generator extends AbstractJavaGenerator{
        private PSet<RClass>    imports = PSet.empty();
        private SourceGen       header = new SourceGen();


        public Generator(SubstemaCompiler compiler, String packageName) {
            super(compiler,packageName);

        }

        private RClass toExprClass(RClass cls){
            return cls.withClassName("_" + cls.getClassName()).withPackageName(packageName);
        }

        public GeneratedJava    generateValueClass(RValueClass vc){
            RClass vcCls = vc.getTypeSig().getName();
            RClass cls = toExprClass(vcCls);


            addImport(vcCls);
            addImport(Expr.class);
            addImport(ETypeObject.class);

            // ********   Table Definition class

            bs("public class " + cls.getClassName() + " implements ETypeObject<" + vcCls.getClassName() + ">");
            {
                // ************ Construction & Parent Expression
                addImport(DbSql.class);
                println("private final Expr __parent;");

                println("");
                bs("public " + cls.getClassName() + "(Expr parent)");{
                println("this.__parent = parent;");
            }be();
                println("");
                bs("public " + cls.getClassName() + "()");{
                println("this(null);");

            }be();
                println("");
                addImport(Optional.class);
                println("@Override");
                println("public Optional<Expr<?>> getParent() { return Optional.ofNullable(this.__parent); }");
                // *****************  _getTableName

                println("");
                println("@Override");
                bs("public String _getTableName()");{
                    String name = atUtils.getOneAnnotation(vc.getAnnotations(),rclassTable)
                            .map(at -> atUtils.getStringProperty(at,"name").orElse(null)).orElse(null);
                    if(name == null) { name = vcCls.getClassName(); }
                    println("return \"" + name + "\";");
                }be();


                // *****************  toString

                println("");
                println("@Override");
                println("public String toString() { return getInstanceName(); }");

                println("");

                // ***************** properties

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
                // **************** read
                addImport(RowReader.class);
                addImport(ExprRowReaderCache.class);
                bs("static public " + vcCls.getClassName() + " read(RowReader _rowReader, " + ExprRowReaderCache.class.getSimpleName() + " _cache)");{
                    vc.getProperties().forEach(p -> {
                        RClass pcls = p.getValueType().getTypeSig().getName();

                        String javaClassName = JavaGenUtils.toString(packageName,pcls);
                        //For internal Substem classes
                        if(SubstemaUtils.isSubstemaClass(pcls)){
                            if(pcls.equals(SubstemaUtils.dateTimeRClass)){
                                addImport(LocalDateTime.class);
                                javaClassName  = LocalDateTime.class.getSimpleName();
                            }
                            if(pcls.equals(SubstemaUtils.dateRClass)){
                                addImport(LocalDate.class);
                                javaClassName = LocalDate.class.getSimpleName();
                            }
                            println(javaClassName + " " + p.getName() + " = _rowReader.readNext("+ javaClassName + ".class);");
                        } else {

                            if(getInternalOrExternalEnum(pcls).isPresent()){
                                //For Enums
                                //Gender gender = Gender.valueOf(rowReader.readNext(String.class));
                                addImport(pcls);
                                String enumStringName = "_" + p.getName() + "String";
                                println("String " + enumStringName +" = _rowReader.readNext(String.class);");
                                println(pcls.getClassName() + " " + p.getName() + " = " + enumStringName +"== null ? null : "+ pcls.getClassName() + ".valueOf(" + enumStringName + ");");
                            } else {
                                addImport(pcls);
                                javaClassName = JavaGenUtils.toString(packageName,pcls.withPackageName(packageName));
                                RClass nc = toExprClass(pcls);
                                addImport(nc);
                                println(javaClassName + " " + p.getName() + " = " + nc.getClassName() + ".read(_rowReader,_cache);");
                            }

                        }

                    });
                    String allNull = vc.getProperties().filter(p -> p.getValueType().isRequired()).map(p -> p.getName() + "==null").toString(" || ");
                    if(allNull.isEmpty() == false){
                        println("if(" + allNull+") { return null; }");
                    }
                    println("return _cache.updatedFromCache(" + vcCls.getClassName() + ".build(b-> b");
                    indent();
                        vc.getProperties().forEach(p -> {
                            println(".set" + StringUtils.firstUpperCase(p.getName()) + "(" + p.getName() + ")");
                        });
                    outdent();
                    println("));");

                }be();

                //**************   Auto Generated Key
                generateAutGenKeyFunctions(vc);

            }be();
            return toGenJava(cls);
        }

        private void generateAutGenKeyFunctions(RValueClass vc){
            /*
            public Optional<Expr<?>> _getAutoGenKey(){
                return Optional.of(id);
            }
            public TranslationUser	setAutoGenKey(TranslationUser object, Object value){
                return object.withId((Long)value);
            }
            */
            RProperty autoGenProp = vc.getProperties().find(p -> atUtils.getOneAnnotation(p.getAnnotations(),rclassAutoGen).isPresent()).orElse(null);
            addImport(Optional.class);
            println("@Override");
            bs("public Optional<Expr> _getAutoGenKey()");{
                if(autoGenProp == null){
                    println("return Optional.empty();");
                } else {
                    println("return Optional.of(" + autoGenProp.getName() + ");");
                }
            }be();
            String vcName = vc.getTypeSig().getName().getClassName();
            println("@Override");
            bs("public " + vcName + " _setAutoGenKey(" + vcName + " object, Object value)");{
                if(autoGenProp == null){
                    addImport(PersistSqlException.class);
                    println("throw new PersistSqlException(\" There is no auto generated key for " + vcName+ "\");");
                } else {
                    String typeName =autoGenProp.getValueType().getTypeSig().getName().getClassName();
                    println("return object.with" +
                            StringUtils.firstUpperCase(autoGenProp.getName()) + "((" + typeName + ") value);");
                }
            }be();


        }

        /**
         * Generate the javacode to transform a real property value to an Expr
         * @param p The RProperty
         * @return The java code to add the Expression(s) to the generated code for a PList with name r
         */
        public String generateValueExpr(RProperty p){


            RClass cls = p.getValueType().getTypeSig().getName();
            String getter = "v.get" + StringUtils.firstUpperCase(p.getName()) + "()";
            if(p.getValueType().isRequired() == false){
                getter = getter + ".orElse(null)";
            }
            if(SubstemaUtils.isNumberClass(cls)
                    || cls.equals(SubstemaUtils.booleanRClass)
                    || cls.equals(SubstemaUtils.stringRClass)
                    || SubstemaUtils.isDateClass(cls)
                    ){
                return "r = r.plus(Expr.val(" + getter + "));";
            }else {
                if(getInternalOrExternalEnum(cls).isPresent()){
                    addImport(ExprEnum.class);
                    //We have an enum
                    //r = r.plus(new ExprEnum<Gender>(v.getGender(),Gender.class));
                    String clsName = cls.getClassName();
                    return "r = r.plus(new " + ExprEnum.class.getSimpleName() + "<" + clsName + ">(" + getter + ", " + clsName + ".class));";
                }
                if(cls.getPackageName().isEmpty()){
                    throw new DbJavaGenException("Unknown internal class: "+ cls);
                }
                RClass nc = toExprClass(cls);
                addImport(nc);
                return "r = r.plusAll(" + JavaGenUtils.toString(packageName,nc) + ".asValues(" + getter + "));";
            }
        }

        /**
         * Generate The DB class for this Substema.<br>
         *
         * @param valueClasses All the tables value classes
         * @return The Generated Db java file
         */
        public GeneratedJava generateDb(PList<RValueClass> valueClasses) {

            RClass dbCls = new RClass(packageName,"Db");
            addImport(dbCls);
            //addImport(ExprDb.class);
            addImport(DbSql.class);
            addImport(SQLTransactionRunner.class);
            addImport(DbType.class);
            bs("public class Db extends DbSql");{
                bs("public Db(DbType dbType, SQLTransactionRunner trans)");{
                    println("super(dbType, trans);");
                }be();

                valueClasses.forEach(vc -> {
                    RClass cls = vc.getTypeSig().getName();
                    RClass ecls = toExprClass(cls);
                    addImport(ecls);
                    println("public " + JavaGenUtils.toString(packageName,ecls) + " " + StringUtils.firstLowerCase(cls.getClassName()) + "(){ return new " + JavaGenUtils.toString(packageName,ecls) +"(); }");
                });

            }be();
            return toGenJava(dbCls);
        }

        /**
         * Generate the java code for a property of a db table description value class;
         * @param property The property to generate
         */
        private void generateProperty(RProperty property){

            String type;
            String value;
            RClass cls = property.getValueType().getTypeSig().getName();
            String tableName = property.getName();
            RAnnotation columnAt =atUtils.getOneAnnotation(property.getAnnotations(),rclassColumn).orElse(null);
            if(columnAt != null){
                tableName = atUtils.getStringProperty(columnAt,"name").orElse(tableName);
            }
            if(SubstemaUtils.isNumberClass(cls)){
                addImport(ETypeNumber.class);
                addImport(ExprPropertyNumber.class);
                type = "ETypeNumber<" + cls.getClassName() + ">";
                value = "new ExprPropertyNumber<>(" + cls.getClassName() + ".class,this,\"" + property.getName()  + "\", \"" + tableName+ "\");";
            } else if (cls.equals(SubstemaUtils.booleanRClass)){
                addImport(ETypeBoolean.class);
                addImport(ExprPropertyBoolean.class);
                type = "ETypeBoolean";
                value = "new ExprPropertyBoolean(this,\"" + property.getName() + "\", \"" + tableName+ "\");";
            } else if (cls.equals(SubstemaUtils.stringRClass)){
                addImport(ETypeString.class);
                addImport(ExprPropertyString.class);
                type = "ETypeString";
                value = "new ExprPropertyString(this,\"" + property.getName() + "\", \"" + tableName+ "\");";
            } else if(cls.equals(SubstemaUtils.dateTimeRClass)){
                addImport(LocalDateTime.class);
                addImport(ExprPropertyDateTime.class);
                addImport(ETypeDateTime.class);
                type = "ETypeDateTime";
                value = "new ExprPropertyDateTime(this,\"" + property.getName() + "\", \"" + tableName+ "\");";

            } else if(cls.equals(SubstemaUtils.dateRClass)){
                addImport(LocalDate.class);
                addImport(ExprPropertyDate.class);
                addImport(ETypeDate.class);
                type = "ETypeDate";
                value = "new ExprPropertyDate(this,\"" + property.getName() + "\", \"" + tableName+ "\");";

            }else {
                if(cls.getPackageName().isEmpty()){
                    throw new DbJavaGenException("Unknown internal type:" + cls);
                }

                boolean isEnum = getInternalOrExternalEnum(cls).isPresent();

                if(isEnum){
                    addImport(cls);
                    addImport(ExprPropertyEnum.class);
                    String valueName = ExprPropertyEnum.class.getSimpleName();

                    type = ExprPropertyEnum.class.getSimpleName()+ "<" + cls.getClassName() + ">";
                    value = "new " +  type + "(" + cls.getClassName() + ".class,this,\"" + property.getName() + "\", \""+ tableName+ "\");";
                } else {
                    RClass nc = toExprClass(cls);
                    //addImport(cls);
                    addImport(nc);
                    addImport(ExprProperty.class);
                    type = JavaGenUtils.toString(packageName,nc);
                    String valueClass = cls.getClassName();
                    value = "new " + type + "(new ExprProperty("+ valueClass + ".class,this,\"" + property.getName() + "\", \"" + tableName+ "\"));";
                }


            }
            println("public " + type +" " + property.getName() + " = " + value);
        }


        /**
         * Try to find an enum for a given RClass.<br>
         * if the RClass is for a diferent package, than that package is resolved.
         * @param cls the RClass for the enum.
         * @return the Optional REnum for the class
         */
        private Optional<REnum> getInternalOrExternalEnum(RClass cls){
            if(cls.getPackageName().equals(packageName) == false){
                RSubstema ss = compiler.compile(cls.getPackageName());
                return ss.getEnums().find(e -> e.getName().getClassName().equals(cls.getClassName()));
            } else {
                return getEnum(cls);
            }
        }



        private String toString(RTypeSig sig){
            return toString(sig,false);
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



    }
    /**
     * Find all external value classes and enums that are used
     * in this substema
     * @return Set with RClass's for all used external value classes or enums
     */
    private PSet<RClass> findAllExternalDefinitions() {
        PSet<RClass> found = PSet.empty();
        for(RValueClass vc : substema.getValueClasses()){
            found = findExternalDefinitions(found,vc.getTypeSig().getName());
        }
        return found;
    }

    /**
     * Find external classes and enums used by the provided cls.<br>
     * used enums and substema classes are ignored
     * @param found list of found external classes
     * @param cls   The class to process
     * @return
     */
    private PSet<RClass> findExternalDefinitions(PSet<RClass> found, RClass cls){
        if(found.contains(cls)){
            return found;
        }
        if(SubstemaUtils.isSubstemaClass(cls)){
            return found;
        }

        if(cls.getPackageName().equals(substema.getPackageName()) == false){
            found = found.plus(cls);
        }


        RValueClass vc = getValueClass(cls).orElse(null);
        if(vc == null){

            return found;
        }

        for(RProperty prop : vc.getProperties()){

            found = found.plusAll(findExternalDefinitions(found,prop.getValueType().getTypeSig().getName()));
        }
        return found;
    }

    /**
     * Return the optional RValueClass defined in the substema substema by checking the RClass
     * @param cls The Value class to find
     * @return empty or some RValueClass
     */
    private Optional<RValueClass> getValueClass(RClass cls){
        return substema.getValueClasses().find(vc -> vc.getTypeSig().getName().equals(cls));
    }

    /**
     * Return the optional REnum defined in the substema substem by checking the RClass
     * @param cls The RClass to find
     * @return empty or some REnum
     */
    private Optional<REnum> getEnum(RClass cls){
        return substema.getEnums().find(vc -> vc.getName().equals(cls));
    }


}
