package com.persistentbit.sql.staticsql.codegen;


import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PSet;
import com.persistentbit.core.sourcegen.SourceGen;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.utils.StringUtils;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.staticsql.DbSql;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;
import com.persistentbit.sql.staticsql.expr.*;
import com.persistentbit.sql.transactions.TransactionRunner;
import com.persistentbit.substema.compiler.SubstemaCompiler;
import com.persistentbit.substema.compiler.SubstemaUtils;
import com.persistentbit.substema.compiler.values.*;
import com.persistentbit.substema.compiler.values.expr.RConstEnum;
import com.persistentbit.substema.javagen.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

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
    static public final RClass rclassNameToLower = new RClass(packageDbAnnotations,"NameToLower");
    static public final RClass rclassNameToUpper = new RClass(packageDbAnnotations,"NameToUpper");
    static public final RClass rclassCamelToSnake = new RClass(packageDbAnnotations,"NameCamelToSnake");
    static public final RClass rclassPrefix = new RClass(packageDbAnnotations,"NamePrefix");
    static public final RClass rclassPostfix = new RClass(packageDbAnnotations,"NamePostfix");
    static public final RClass rclassNoPrefix = new RClass(packageDbAnnotations,"NoPrefix");

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
     * @param packageName   package to generate java for.
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

            Function<String,String> tableNameConverter = createNameConverter(substema.getPackageDef().getAnnotations(),NameType.table);

            addImport(vcCls);
            addImport(Expr.class);
            addImport(Sql.class);
            addImport(ETypeObject.class);
            boolean isTableClass = atUtils.hasAnnotation(vc.getAnnotations(),rclassTable);

            // ********   Table Definition class

            bs("public class " + cls.getClassName() + " implements ETypeObject<" + vcCls.getClassName() + ">");
            {
                // ************ Construction & Parent Expression
                addImport(DbSql.class);
                addImport(ETypePropertyParent.class);
                println("private final ETypePropertyParent __parent;");
                if(isTableClass) {
                    println("private final DbSql _db;");
                    println("");
                    bs("public " + cls.getClassName() + "(DbSql db, ETypePropertyParent parent)");{
                        println("this._db = db;");
                        println("this.__parent = parent;");
                    }be();
                    println("");
                    bs("public " + cls.getClassName() + "(DbSql db)");{
                        println("this(db,null);");
                    }be();
                } else {
                    bs("public " + cls.getClassName() + "(ETypePropertyParent parent)");{
                        println("this.__parent = parent;");
                    }be();
                    println("");
                    bs("public " + cls.getClassName() + "()");{
                        println("this(null);");
                    }be();

                }



                println("");
                addImport(Optional.class);
                println("@Override");
                println("public Optional<ETypePropertyParent> getParent() { return Optional.ofNullable(this.__parent); }");
                // *****************  _getTableName

                println("");
                println("@Override");
                bs("public String _getTableName()");{
                    String name = atUtils.getOneAnnotation(vc.getAnnotations(),rclassTable)
                            .map(at -> atUtils.getStringProperty(at,"name").orElse(null)).orElse(null);
                    if(name == null) { name = tableNameConverter.apply(vcCls.getClassName()); }
                    println("return \"" + name + "\";");
                }be();


                // *****************  toString

                println("");
                println("@Override");
                println("public String toString() { return getInstanceName(); }");

                println("");

                // ***************** _fullColumnName
                addImport(ExprToSqlContext.class);
                println("");
                println("@Override");
                println("public String _fullColumnName(ExprToSqlContext context) { return __parent == null ? \"\" : __parent._fullColumnName(context); }");

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
                // ***************** _asExprValues
                println("");
                bs("public PList<Expr<?>> _asExprValues(" + vcCls.getClassName() + " v)");{
                    println("return " + cls.getClassName() + ".asValues(v);");
                }be();

                bs("static public PList<Expr<?>> asValues(" + vcCls.getClassName() + " v)");{
                println("PList<Expr<?>> r = PList.empty();");
                vc.getProperties().forEach(p -> {
                    println(generateValueExpr(p));
                });
                println("return r;");
            }be();

                // ***************** _expand
                println("");
                bs("public PList<Expr<?>> _expand()");{
                     println("PList<Expr<?>> res = PList.empty();");
                     vc.getProperties().forEach(p -> {
                         println("res = res.plusAll(" + p.getName() + "._expand());");
                     });
                     println("return res;");
                }be();


                // **************** read
                addImport(RowReader.class);
                addImport(ExprRowReaderCache.class);
                bs("public " + vcCls.getClassName() + " read(RowReader _rowReader, " + ExprRowReaderCache.class.getSimpleName() + " _cache)");{
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
                            println(javaClassName + " " + p.getName() + " = this." + p.getName() + ".read(_rowReader,_cache);");
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
                                println(javaClassName + " " + p.getName() + " = this." + p.getName() + ".read(_rowReader,_cache);");
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

                //**************   Insert
                if(isTableClass){
                    generateInsertFunction(vc);
                }
                //*************   Select
                if(isTableClass){
                    generateSelectByIdFunction(vc);
                }
                //*************  Delete
                if(isTableClass){
                    generateDeleteByIdFunction(vc);
                }
                //*************  Update
                if(isTableClass){
                    generateUpdateFunction(vc);
                }

            }be();
            return toGenJava(cls);
        }

        private void generateUpdateFunction(RValueClass vc){
            PList<RProperty> keys = vc.getProperties().filter(p -> atUtils.hasAnnotation(p.getAnnotations(),rclassKey));
            if(keys.isEmpty()){
                return;
            }

            String vcName = vc.getTypeSig().getName().getClassName();
            bs("public " + vcName + " update(" + vcName + " _row)");{
                println("int count = _db.update(this)");

                vc.getProperties()
                        .filter(p -> atUtils.hasAnnotation(p.getAnnotations(),rclassKey) == false)
                        .filter(p -> atUtils.hasAnnotation(p.getAnnotations(),rclassAutoGen) == false)
                        .forEach(p -> {
                    RClass cls = p.getValueType().getTypeSig().getName();
                    String getter = "_row.get" + StringUtils.firstUpperCase(p.getName()) + "()";
                    if(p.getValueType().isRequired() == false){
                        getter = getter + ".orElse(null)";
                    }
                    String valExpr;
                    if(SubstemaUtils.isSubstemaClass(cls)){
                        valExpr = "Sql.val(" + getter + ")";
                    } else {
                        if(getInternalOrExternalEnum(cls).isPresent()){
                            addImport(ExprEnum.class);
                            String clsName = cls.getClassName();
                            valExpr =  "new " + ExprEnum.class.getSimpleName() + "<" + clsName + ">(" + getter + ", " + clsName + ".class)";
                        } else {
                            addImport(EValTable.class);
                            valExpr = "new EValTable(" + p.getName() + ", " + getter + ")";
                        }
                    }
                    println(".set(" + p.getName() + ", " + valExpr + ")");
                });

                String cond = keys.headOpt().map(p ->  "this." + p.getName() + ".eq(" + getValGetter(p,"_row") + ")").get();
                cond = cond + keys.tail().map(p ->  ".and(this." + p.getName() + ".eq(" + getValGetter(p,"_row") + "))").toString("");
                println(".where(" + cond + ")");
                println(".execute();");
                bs("if(count != 1)");{
                    addImport(PersistSqlException.class);
                    println("throw new PersistSqlException(\"Expected 1 row updated, not \" + count + \" for \" + _row);");
                }be();
                println("return _row;");
            }be();
        }

        private String getValGetter(RProperty p, String valueName){
            RClass cls = p.getValueType().getTypeSig().getName();
            String getter = valueName + ".get" + StringUtils.firstUpperCase(p.getName()) + "()";
            if(p.getValueType().isRequired() == false){
                getter = getter + ".orElse(null)";
            }
            return getter;
        }

        private void generateDeleteByIdFunction(RValueClass vc){
            PList<RProperty> keys = vc.getProperties().filter(p -> atUtils.hasAnnotation(p.getAnnotations(),rclassKey));
            if(keys.isEmpty()){
                return;
            }
            PList<Tuple2<String,String>> keyTypesAndNames = keys.map(p ->
                    Tuple2.of(p.getValueType().getTypeSig().getName().getClassName(), p.getName())
            );
            addImport(Optional.class);

            bs("public int deleteById(" + keyTypesAndNames.map(t -> t._1 + " " + t._2).toString(", ") + ")");{
                String cond = keyTypesAndNames.headOpt().map(tn ->  "this." + tn._2 + ".eq(" + tn._2 + ")").get();
                cond = cond + keyTypesAndNames.tail().map(tn ->  ".and(this." + tn._2 + ".eq(" + tn._2 + "))").toString("");
                println("return _db.deleteFrom(this).where(" + cond + ").execute();");
            }be();
        }
        private void generateSelectByIdFunction(RValueClass vc){
            PList<RProperty> keys = vc.getProperties().filter(p -> atUtils.hasAnnotation(p.getAnnotations(),rclassKey));
            if(keys.isEmpty()){
                return;
            }
            PList<Tuple2<String,String>> keyTypesAndNames = keys.map(p ->
                    Tuple2.of(p.getValueType().getTypeSig().getName().getClassName(), p.getName())
            );
            addImport(Optional.class);
            String vcName = vc.getTypeSig().getName().getClassName();
            bs("public Optional<" + vcName + "> selectById(" + keyTypesAndNames.map(t -> t._1 + " " + t._2).toString(", ") + ")");{
                String cond = keyTypesAndNames.headOpt().map(tn ->  "this." + tn._2 + ".eq(" + tn._2 + ")").get();
                cond = cond + keyTypesAndNames.tail().map(tn ->  ".and(this." + tn._2 + ".eq(" + tn._2 + "))").toString("");
                println("return _db.queryFrom(this).where(" + cond + ").selection(this).getOneResult();");
            }be();
        }

        private void generateAutGenKeyFunctions(RValueClass vc){

            RProperty autoGenProp = vc.getProperties().find(p -> atUtils.getOneAnnotation(p.getAnnotations(),rclassAutoGen).isPresent()).orElse(null);
            addImport(Optional.class);
            bs("public Optional<Expr<?>> _getAutoGenKey()");{
                if(autoGenProp == null){
                    println("return Optional.empty();");
                } else {
                    println("return Optional.of(" + autoGenProp.getName() + ");");
                }
            }be();
            String vcName = vc.getTypeSig().getName().getClassName();
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

        private void generateInsertFunction(RValueClass vc){
            RProperty autoGenProp = vc.getProperties().find(p -> atUtils.getOneAnnotation(p.getAnnotations(),rclassAutoGen).isPresent()).orElse(null);
            String vcName = vc.getTypeSig().getName().getClassName();
            bs("public " + vcName + " insert(" + vcName + " newRow)");{
                if(autoGenProp == null){
                    println("_db.runInsert(this,val(newRow));");
                    println("return newRow;");
                } else {
                    println("return _db.runInsertWithGenKeys(this,newRow,_getAutoGenKey().get(),this::_setAutoGenKey);");
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
                return "r = r.plus(Sql.val(" + getter + "));";
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
            addImport(TransactionRunner.class);
            addImport(DbType.class);
            bs("public class Db extends DbSql");{
                bs("public Db(DbType dbType, "+ TransactionRunner.class.getSimpleName() + " trans)");{
                    println("super(dbType, trans);");
                }be();

                valueClasses.forEach(vc -> {
                    RClass cls = vc.getTypeSig().getName();
                    RClass ecls = toExprClass(cls);
                    addImport(ecls);
                    boolean isTableClass = atUtils.hasAnnotation(vc.getAnnotations(),rclassTable);
                    if(isTableClass){
                        println("public " + JavaGenUtils.toString(packageName,ecls) + " " + StringUtils.firstLowerCase(cls.getClassName()) + "(){ return new " + JavaGenUtils.toString(packageName,ecls) +"(this); }");
                    } else {
                        println("public " + JavaGenUtils.toString(packageName,ecls) + " " + StringUtils.firstLowerCase(cls.getClassName()) + "(){ return new " + JavaGenUtils.toString(packageName,ecls) +"(); }");
                    }

                });

            }be();
            return toGenJava(dbCls);
        }

        /**
         * Generate the java code for a property of a db table description value class;
         * @param property The property to generate
         */
        private void generateProperty(RProperty property){
            Function<String,String> columnNameConverter = createNameConverter(substema.getPackageDef().getAnnotations(),NameType.column);
            String type;
            String value;
            RClass cls = property.getValueType().getTypeSig().getName();
            String columnName = property.getName();
            RAnnotation columnAt =atUtils.getOneAnnotation(property.getAnnotations(),rclassColumn).orElse(null);
            if(columnAt != null){
                columnName = atUtils.getStringProperty(columnAt,"name").orElse(columnName);
            } else {
                columnName = columnNameConverter.apply(columnName);
            }
            if(SubstemaUtils.isNumberClass(cls)){
                addImport(ETypeNumber.class);
                addImport(ExprPropertyNumber.class);
                type = "ETypeNumber<" + cls.getClassName() + ">";
                value = "new ExprPropertyNumber<>(" + cls.getClassName() + ".class,this,\"" + property.getName()  + "\", \"" + columnName+ "\");";
            } else if (cls.equals(SubstemaUtils.booleanRClass)){
                addImport(ETypeBoolean.class);
                addImport(ExprPropertyBoolean.class);
                type = "ETypeBoolean";
                value = "new ExprPropertyBoolean(this,\"" + property.getName() + "\", \"" + columnName+ "\");";
            } else if (cls.equals(SubstemaUtils.stringRClass)){
                addImport(ETypeString.class);
                addImport(ExprPropertyString.class);
                type = "ETypeString";
                value = "new ExprPropertyString(this,\"" + property.getName() + "\", \"" + columnName+ "\");";
            } else if(cls.equals(SubstemaUtils.dateTimeRClass)){
                addImport(LocalDateTime.class);
                addImport(ExprPropertyDateTime.class);
                addImport(ETypeDateTime.class);
                type = "ETypeDateTime";
                value = "new ExprPropertyDateTime(this,\"" + property.getName() + "\", \"" + columnName+ "\");";

            } else if(cls.equals(SubstemaUtils.dateRClass)){
                addImport(LocalDate.class);
                addImport(ExprPropertyDate.class);
                addImport(ETypeDate.class);
                type = "ETypeDate";
                value = "new ExprPropertyDate(this,\"" + property.getName() + "\", \"" + columnName+ "\");";

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
                    value = "new " +  type + "(" + cls.getClassName() + ".class,this,\"" + property.getName() + "\", \""+ columnName+ "\");";
                } else {
                    RClass nc = toExprClass(cls);
                    //addImport(cls);
                    addImport(nc);
                    addImport(ExprProperty.class);
                    type = JavaGenUtils.toString(packageName,nc);
                    String valueClass = cls.getClassName();
                    //Check if we have a @NoPrefix on the property.
                    RAnnotation noPrefixAt =atUtils.getOneAnnotation(property.getAnnotations(),rclassNoPrefix)
                            .orElseGet(() ->
                                    atUtils.getOneAnnotation(findValueClass(cls).getAnnotations(),rclassNoPrefix)
                                            .orElse(null)
                            );

                    if(noPrefixAt != null){
                        columnName = "";
                    }

                    value = "new " + type + "(new ExprProperty("+ valueClass + ".class,this,\"" + property.getName() + "\", \"" + columnName+ "\"));";
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


        private Function<String,String> createNameConverter(PList<RAnnotation> annotations,NameType type){
            //Find all anotations
            PList<RAnnotation> nameAt = annotations.filter(a ->
                a.getName().equals(rclassCamelToSnake) ||
                a.getName().equals(rclassNameToLower) ||
                a.getName().equals(rclassNameToUpper) ||
                a.getName().equals(rclassPostfix) ||
                a.getName().equals(rclassPrefix)
            );
            Function<String,String> res = i -> i;
            for(RAnnotation at : nameAt){
                RConstEnum cenum = (RConstEnum)atUtils.getProperty(at,"type").get();
                String atTypeName = cenum.getEnumValue();

                boolean ok =    (type == NameType.table && atTypeName.equals("table")) ||
                                (type==NameType.column && atTypeName.equals("column")) ||
                                atTypeName.equals("all");
                if(ok == false){
                    continue;
                }
                switch(at.getName().getClassName()){
                    case "NameToLower":
                        res = res.andThen(s -> s.toLowerCase());
                        break;
                    case "NameToUpper":
                        res = res.andThen(s -> s.toUpperCase());
                        break;
                    case "NameCamelToSnake":
                        res = res.andThen(s -> StringUtils.camelCaseTo_snake(s));
                        break;
                    case "NamePrefix":
                        String prefix = atUtils.getStringProperty(at,"value").orElse(null);
                        res = res.andThen(s -> prefix + s);
                        break;
                    case "NamePostfix":
                        String postFix = atUtils.getStringProperty(at,"value").orElse(null);
                        res = res.andThen(s -> s + postFix);
                        break;
                    default: throw new PersistSqlException("Unknown:" + cenum.getEnumClass());
                }
            }
            return res;
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
     * Return the optional RValueClass defined in the current substema by checking the RClass
     * @param cls The Value class to find
     * @return empty or some RValueClass
     */
    private Optional<RValueClass> getValueClass(RClass cls){
        return substema.getValueClasses().find(vc -> vc.getTypeSig().getName().equals(cls));
    }

    /**
     * Find a  value class internally or externally...
     * @param cls the RClass to find
     * @return  The Optional cls
     */
    private RValueClass findValueClass(RClass cls){
        if(cls.getPackageName() == substema.getPackageName()){
            return getValueClass(cls).orElseThrow(()->new PersistSqlException("Can't find Internal Value class " + cls));
        }
        RSubstema ns = compiler.compile(cls.getPackageName());
        return ns.getValueClasses().find(vc-> vc.getTypeSig().getName().equals(cls)).orElseThrow(()-> new PersistSqlException("Can't find Value class " + cls));
    }

    /**
     * Return the optional REnum defined in the substema substem by checking the RClass
     * @param cls The RClass to find
     * @return empty or some REnum
     */
    private Optional<REnum> getEnum(RClass cls){
        return substema.getEnums().find(vc -> vc.getName().equals(cls));
    }

    private enum  NameType{
        column,table
    }
}
