package com.persistentbit.sql.staticsql.codegen;


import com.persistentbit.core.collections.PByteList;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PSet;
import com.persistentbit.core.logging.Log;
import com.persistentbit.core.result.Result;
import com.persistentbit.core.sourcegen.SourceGen;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.utils.UString;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.staticsql.*;
import com.persistentbit.sql.staticsql.expr.*;
import com.persistentbit.sql.transactions.TransactionRunner;
import com.persistentbit.substema.compiler.SubstemaCompiler;
import com.persistentbit.substema.compiler.SubstemaException;
import com.persistentbit.substema.compiler.SubstemaUtils;
import com.persistentbit.substema.compiler.values.*;
import com.persistentbit.substema.javagen.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

import static com.persistentbit.sql.staticsql.codegen.DbAnnotationsUtils.*;

/**
 * Generate Java code for a Database Substema
 *
 * @author Peter Muys
 * @since 14/09/16
 */
public final class DbJavaGen{


	private final JavaGenOptions   options;
	private final RSubstema        substema;
	private final SubstemaCompiler compiler;
	private final PSet<RClass>     allExternalValueClasses;

	private DbJavaGen(JavaGenOptions options, String packageName, SubstemaCompiler compiler) {
		this.options = options;

		this.compiler = compiler;
		this.substema = this.compiler.compile(packageName).orElseThrow();

		this.allExternalValueClasses = findAllExternalDefinitions();
	}

	/**
	 * Generate all java classes for this DB Substema.
	 *
	 * @param options     Code gen options
	 * @param packageName package to generate java for.
	 * @param compiler    The substema compiler
	 *
	 * @return PList with all the generated java files
	 */
	public static PList<Result<GeneratedJava>> generate(JavaGenOptions options, String packageName,
														SubstemaCompiler compiler
	) {
		return Log.function(options, packageName, compiler).code(l ->
																	 new DbJavaGen(options, packageName, compiler)
																		 .generateService()
		);

	}

	/**
	 * Generate all java classes for this DB Substema.
	 *
	 * @return PList with all the generated java files
	 */
	public PList<Result<GeneratedJava>> generateService() {
		return Log.function().code(log -> {
			PList<Result<GeneratedJava>> tableValueClasses = SubstemaJavaGen.generate(compiler, options, substema);
			tableValueClasses.forEach(r -> log.add(r));


			//Generate value classes defined in this substema
			PList<Result<GeneratedJava>> generatedForThisSubstema =
				substema.getValueClasses()
					.map(vc -> new Generator(compiler, substema.getPackageName()).generateValueClass(vc)
					);

			generatedForThisSubstema.forEach(r -> log.add(r));

			//For all external case classes that are used
			//in this substema, generate the db description
			PList<Result<GeneratedJava>> generatedDescriptions =
				allExternalValueClasses.map(c -> {
					if(c.getPackageName().isEmpty()) {
						//must be an enum;
						return null;
					}
					RSubstema substema = compiler.compile(c.getPackageName()).orElseThrow();
					RValueClass vc =
						substema.getValueClasses().find(evc -> evc.getTypeSig().getName().equals(c)).orElse(null);
					if(vc == null) {
						return null;
					}
					return new Generator(compiler, this.substema.getPackageName()).generateValueClass(vc);
				}).filterNulls().plist();
			generatedDescriptions.forEach(r -> log.add(r));

			Result<GeneratedJava> generatedDbClass =
				new Generator(compiler, substema.getPackageName()).generateDb(substema.getValueClasses());

			log.add(generatedDbClass);


			return PList.val(generatedDbClass)
				.plusAll(generatedForThisSubstema)
				.plusAll(generatedDescriptions)
				.plusAll(tableValueClasses)
				;
		});
	}

	/**
	 * Find all external value classes and enums that are used
	 * in this substema
	 *
	 * @return Set with RClass's for all used external value classes or enums
	 */
	private PSet<RClass> findAllExternalDefinitions() {
		PSet<RClass> found = PSet.empty();
		for(RValueClass vc : substema.getValueClasses()) {
			found = findExternalDefinitions(found, vc.getTypeSig().getName());
		}
		return found;
	}

	/**
	 * Find external classes and enums used by the provided cls.<br>
	 * used enums and substema classes are ignored
	 *
	 * @param foundList list of found external classes
	 * @param cls   The class to process
	 *
	 * @return Set of RClass
	 */
	private PSet<RClass> findExternalDefinitions(PSet<RClass> foundList, RClass cls) {
		return Log.function("<foundList>", cls).code(l -> {
			if(foundList.contains(cls)) {
				return foundList;
			}
			if(SubstemaUtils.isSubstemaClass(cls)) {
				return foundList;
			}
			PSet<RClass> result = foundList;
			if(cls.getPackageName().equals(substema.getPackageName()) == false) {
				result = result.plus(cls);
				RSubstema   extSubstema = compiler.compile(cls.getPackageName()).orElseThrow();
				RValueClass vc          =
					extSubstema.getValueClasses().find(evc -> evc.getTypeSig().getName().equals(cls)).orElse(null);
				if(vc == null) {
					extSubstema.getEnums().find(ec -> ec.getName().equals(cls))
						.orElseThrow(() -> new SubstemaException("Expected an enum or value class for " + cls));
					return result;//Must be an enum
				}
				for(RProperty prop : vc.getProperties()) {
					RClass searchClass = prop.getValueType().getTypeSig().getName();
					//System.out.println("Looking for " + searchClass);
					if(searchClass.equals(cls) == false) {
						result = result.plusAll(findExternalDefinitions(result, searchClass));
					}
				}
				return result;
			}


			RValueClass vc = getValueClass(cls).orElse(null);
			if(vc == null) {

				return result;
			}

			for(RProperty prop : vc.getProperties()) {
				RClass searchClass = prop.getValueType().getTypeSig().getName();
				//System.out.println("Looking for " + searchClass);
				if(searchClass.equals(cls) == false) {
					result = result.plusAll(findExternalDefinitions(result, searchClass));
				}
			}
			return result;
		});
	}



	/**
	 * Return the optional RValueClass defined in the current substema by checking the RClass
	 *
	 * @param cls The Value class to find
	 *
	 * @return empty or some RValueClass
	 */
	private Optional<RValueClass> getValueClass(RClass cls) {
		return substema.getValueClasses().find(vc -> vc.getTypeSig().getName().equals(cls));
	}


	private final class Generator extends AbstractJavaGenerator{

		private final PSet<RClass> imports = PSet.empty();
		private final SourceGen    header  = new SourceGen();


		private Generator(SubstemaCompiler compiler, String packageName) {
			super(compiler, packageName);


		}

		public Result<GeneratedJava> generateValueClass(RValueClass vc) {
			return Result.function(vc.getTypeSig()).code(log -> {
				RClass vcCls = vc.getTypeSig().getName();
				RClass cls   = toExprClass(vcCls);

				Function<String, String> tableNameConverter =
					createSubstemaToDbNameConverter(substema.getPackageDef().getAnnotations(), NameType.table, atUtils);

				addImport(vcCls);
				addImport(Expr.class);
				addImport(Sql.class);
				addImport(ETypeObject.class);
				boolean isTableClass = atUtils.hasAnnotation(vc.getAnnotations(), rclassTable);

				// ********   Table Definition class
				generateJavaDoc(vc.getAnnotations());
				bs("public class " + cls.getClassName() + " implements ETypeObject<" + vcCls.getClassName() + ">");
				{
					// ************ Construction & Parent Expression
					//addImport(DbSql.class);
					addImport(ETypePropertyParent.class);
					println("private final ETypePropertyParent __parent;");
					{
						bs("public " + cls.getClassName() + "(ETypePropertyParent parent)");
						{
							println("this.__parent = parent;");
						}
						be();
						println("");
						bs("public " + cls.getClassName() + "()");
						{
							println("this(null);");
						}
						be();

					}


					println("");
					addImport(Optional.class);
					println("@Override");
					println("public Optional<ETypePropertyParent> getParent() { return Optional.ofNullable(this.__parent); }");
					// *****************  _getTableName

					println("");
					println("@Override");
					bs("public String _getTableName()");
					{
						String name = atUtils.getOneAnnotation(vc.getAnnotations(), rclassTable)
							.map(at -> atUtils.getStringProperty(at, "name").orElse(null)).orElse(null);
						if(name == null) { name = tableNameConverter.apply(vcCls.getClassName()); }
						println("return \"" + name + "\";");
					}
					be();


					// *****************  toString

					println("");
					println("@Override");
					{
						println("public String toString() { return _getTableName(); }");
					}


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
					bs("public PList<Tuple2<String,Expr<?>>> _all()");
					{
						if(vc.getProperties().isEmpty()) {
							println("return PList.empty();");
						}
						else {
							println("return PList.val(" + vc.getProperties()
								.map(p -> "Tuple2.of(\"" + p.getName() + "\"," + p.getName() + ")")
								.toString(", ") + ");");
						}
					}
					be();
					// ***************** _asExprValues
					println("");
					bs("public PList<Expr<?>> _asExprValues(" + vcCls.getClassName() + " v)");
					{
						println("return " + cls.getClassName() + ".asValues(v);");
					}
					be();

					bs("static public PList<Expr<?>> asValues(" + vcCls.getClassName() + " v)");
					{
						println("PList<Expr<?>> r = PList.empty();");
						vc.getProperties().forEach(p -> println(generateValueExpr(p)));
						println("return r;");
					}
					be();

					// ***************** _expand
					println("");
					bs("public PList<Expr<?>> _expand()");
					{
						println("PList<Expr<?>> res = PList.empty();");
						vc.getProperties().forEach(p -> println("res = res.plusAll(" + p.getName() + "._expand());"));
						println("return res;");
					}
					be();


					// **************** read
					addImport(RowReader.class);
					addImport(ExprRowReaderCache.class);
					bs("public " + vcCls.getClassName() + " read(RowReader _rowReader, " + ExprRowReaderCache.class
						.getSimpleName() + " _cache)");
					{
						vc.getProperties().forEach(p -> {
							RClass propCls = p.getValueType().getTypeSig().getName();

							String javaClassName = JavaGenUtils.toString(packageName, propCls);
							//For internal Substema classes
							if(SubstemaUtils.isSubstemaClass(propCls)) {
								if(propCls.equals(SubstemaUtils.dateTimeRClass)) {
									addImport(LocalDateTime.class);
									javaClassName = LocalDateTime.class.getSimpleName();
								}
								if(propCls.equals(SubstemaUtils.dateRClass)) {
									addImport(LocalDate.class);
									javaClassName = LocalDate.class.getSimpleName();
								}
								println(javaClassName + " " + p.getName() + " = this." + p
									.getName() + ".read(_rowReader,_cache);");
							}
							else {

								if(getInternalOrExternalEnum(propCls).isPresent()) {
									//For Enums
									//Gender gender = Gender.valueOf(rowReader.readNext(String.class));
									addImport(propCls);
									String enumStringName = "_" + p.getName() + "String";
									println("String " + enumStringName + " = _rowReader.readNext(String.class);");
									println(propCls.getClassName() + " " + p
										.getName() + " = " + enumStringName + "== null ? null : " + propCls
										.getClassName() + ".valueOf(" + enumStringName + ");");
								}
								else {
									addImport(propCls);
									javaClassName =
										JavaGenUtils.toString(packageName, propCls.withPackageName(packageName));
									RClass nc = toExprClass(propCls);
									addImport(nc);
									println(javaClassName + " " + p.getName() + " = this." + p
										.getName() + ".read(_rowReader,_cache);");
								}

							}
						});

						String allNull =
							vc.getProperties().filter(p -> p.getValueType().isRequired())
								.map(p -> p.getName() + "==null")
								.toString(" || ");
						if(allNull.isEmpty() == false) {
							println("if(" + allNull + ") { return null; }");
						}
						println("return _cache.updatedFromCache(" + vcCls.getClassName() + ".build(b-> b");
						indent();
						vc.getProperties()
						  .forEach(p -> println(".set" + UString.firstUpperCase(p.getName()) + "(" + p
								.getName() + ")"));
						outdent();
						println("));");

					}
					be();
					//************** Query
					addImport(Query.class);
					bs("public\tQuery\tquery()");{
						println("return Query.from(this);");
					}be();

					//**************   Auto Generated Key
					generateAutGenKeyFunctions(vc);

					//**************   Insert
					if(isTableClass) {
						generateInsertFunction(vc);
					}
					//*************   Select
					if(isTableClass) {
						generateSelectByIdFunction(vc);
					}
					//*************  Delete
					if(isTableClass) {
						generateDeleteByIdFunction(vc);
					}
					//*************  Update
					if(isTableClass) {
						generateUpdateFunction(vc);
					}

				}
				be();
				return toGenJava(cls);

			});
		}

		/**
		 * Generate the java code to transform a real property value to an Expr
		 *
		 * @param p The RProperty
		 *
		 * @return The java code to add the Expression(s) to the generated code for a PList with name r
		 */
		public String generateValueExpr(RProperty p) {


			RClass cls    = p.getValueType().getTypeSig().getName();
			String getter = "v.get" + UString.firstUpperCase(p.getName()) + "()";
			if(p.getValueType().isRequired() == false) {
				getter = getter + ".orElse(null)";
			}
			if(SubstemaUtils.isNumberClass(cls)
				|| cls.equals(SubstemaUtils.booleanRClass)
				|| cls.equals(SubstemaUtils.stringRClass)
				|| SubstemaUtils.isDateClass(cls)
				|| cls.equals(SubstemaUtils.binaryRClass)
				) {
				return "r = r.plus(Sql.val(" + getter + "));";
			}
			else {
				if(getInternalOrExternalEnum(cls).isPresent()) {
					addImport(ExprValueEnum.class);
					//We have an enum
					//r = r.plus(new ExprEnum<Gender>(v.getGender(),Gender.class));
					String clsName = cls.getClassName();
					return "r = r.plus(new " + ExprValueEnum.class
						.getSimpleName() + "<" + clsName + ">(" + getter + ", " + clsName + ".class));";
				}
				if(cls.getPackageName().isEmpty()) {
					throw new DbJavaGenException("Unknown internal class: " + cls);
				}
				RClass nc = toExprClass(cls);
				addImport(nc);
				return "r = r.plusAll(" + JavaGenUtils.toString(packageName, nc) + ".asValues(" + getter + "));";
			}
		}

		private void generateAutGenKeyFunctions(RValueClass vc) {

			RProperty autoGenProp =
				vc.getProperties().find(p -> atUtils.getOneAnnotation(p.getAnnotations(), rclassAutoGen).isPresent())
					.orElse(null);
			addImport(Optional.class);
			bs("public Optional<Expr<?>> _getAutoGenKey()");
			{
				if(autoGenProp == null) {
					println("return Optional.empty();");
				}
				else {
					println("return Optional.of(" + autoGenProp.getName() + ");");
				}
			}
			be();
			String vcName = vc.getTypeSig().getName().getClassName();
			bs("public " + vcName + " _setAutoGenKey(" + vcName + " object, Object value)");
			{
				if(autoGenProp == null) {
					addImport(PersistSqlException.class);
					println("throw new PersistSqlException(\" There is no auto generated key for " + vcName + "\");");
				}
				else {
					String typeName = autoGenProp.getValueType().getTypeSig().getName().getClassName();
					println("return object.with" +
						UString.firstUpperCase(autoGenProp.getName()) + "((" + typeName + ") value);");
				}
			}
			be();


		}

		private void generateInsertFunction(RValueClass vc) {
			addImport(DbWork.class);
			addImport(Insert.class);
			addImport(Update.class);
			addImport(Delete.class);
			addImport(Update.class);
			RProperty autoGenProp =
				vc.getProperties().find(p -> atUtils.getOneAnnotation(p.getAnnotations(), rclassAutoGen).isPresent())
					.orElse(null);
			String vcName = vc.getTypeSig().getName().getClassName();
			bs("public DbWork<" + vcName + "> insert(" + vcName + " newRow)");
			{
				if(autoGenProp == null) {
					println("return Insert.into(this,val(newRow)).map(count -> newRow);");
				}
				else {
					println("return Insert.into(this,val(newRow)).withGeneratedKeys(_getAutoGenKey().get())");
					println("\t.map(key -> _setAutoGenKey(newRow,key));");
				}

				/*if(autoGenProp == null) {
					println("_db.runInsert(this,val(newRow));");
					println("return newRow;");
				}
				else {
					println("return _db.runInsertWithGenKeys(this,newRow,_getAutoGenKey().get(),this::_setAutoGenKey);");
				}*/
			}
			be();


		}

		private void generateSelectByIdFunction(RValueClass vc) {
			PList<RProperty> keys =
				vc.getProperties().filter(p -> atUtils.hasAnnotation(p.getAnnotations(), rclassKey));
			if(keys.isEmpty()) {
				return;
			}
			PList<Tuple2<String, String>> keyTypesAndNames = keys.map(p ->
																		  Tuple2.of(p.getValueType().getTypeSig()
																						.getName().getClassName(), p
																						.getName())
			);
			addImport(DbWork.class);
			addImport(Query.class);
			addImport(Result.class);
			String vcName = vc.getTypeSig().getName().getClassName();
			bs("public DbWork<" + vcName + "> selectById(" + keyTypesAndNames.map(t -> t._1 + " " + t._2)
				.toString(", ") + ")");
			{
				String cond = keyTypesAndNames.headOpt().map(tn -> "this." + tn._2 + ".eq(" + tn._2 + ")").get();
				cond =
					cond + keyTypesAndNames.tail().map(tn -> ".and(this." + tn._2 + ".eq(" + tn._2 + "))").toString("");
				println("return Query.from(this).where(" + cond + ").selection(this).flatMap(l -> Result.fromOpt(l.headOpt()));");
			}
			be();
		}

		private void generateDeleteByIdFunction(RValueClass vc) {
			PList<RProperty> keys =
				vc.getProperties().filter(p -> atUtils.hasAnnotation(p.getAnnotations(), rclassKey));
			if(keys.isEmpty()) {
				return;
			}
			PList<Tuple2<String, String>> keyTypesAndNames = keys.map(p ->
																		  Tuple2.of(p.getValueType().getTypeSig()
																						.getName().getClassName(), p
																						.getName())
			);
			addImport(DbWork.class);
			addImport(Delete.class);

			bs("public DbWork<Integer> deleteById(" + keyTypesAndNames.map(t -> t._1 + " " + t._2)
				.toString(", ") + ")");
			{
				String cond = keyTypesAndNames.headOpt().map(tn -> "this." + tn._2 + ".eq(" + tn._2 + ")").get();
				cond =
					cond + keyTypesAndNames.tail().map(tn -> ".and(this." + tn._2 + ".eq(" + tn._2 + "))").toString("");
				println("return new Delete(this).where(" + cond + ");");
			}
			be();
		}

		private void generateUpdateFunction(RValueClass vc) {
			PList<RProperty> keys =
				vc.getProperties().filter(p -> atUtils.hasAnnotation(p.getAnnotations(), rclassKey));
			if(keys.isEmpty()) {
				return;
			}
			addImport(DbWork.class);
			String vcName = vc.getTypeSig().getName().getClassName();
			bs("public DbWork<" + vcName + "> update(" + vcName + " _row)");
			{
				println("return new Update(this)");

				vc.getProperties()
					.filter(p -> atUtils.hasAnnotation(p.getAnnotations(), rclassKey) == false)
					.filter(p -> atUtils.hasAnnotation(p.getAnnotations(), rclassAutoGen) == false)
					.forEach(p -> {
						RClass cls    = p.getValueType().getTypeSig().getName();
						String getter = "_row.get" + UString.firstUpperCase(p.getName()) + "()";
						if(p.getValueType().isRequired() == false) {
							getter = getter + ".orElse(null)";
						}
						String valExpr;
						if(SubstemaUtils.isSubstemaClass(cls)) {
							valExpr = "Sql.val(" + getter + ")";
						}
						else {
							if(getInternalOrExternalEnum(cls).isPresent()) {
								addImport(ExprValueEnum.class);
								String clsName = cls.getClassName();
								valExpr = "new " + ExprValueEnum.class
									.getSimpleName() + "<" + clsName + ">(" + getter + ", " + clsName + ".class)";
							}
							else {
								addImport(ExprValueTable.class);
								valExpr = "new " + ExprValueTable.class.getSimpleName() + "(" + p
									.getName() + ", " + getter + ")";
							}
						}
						println(".set(" + p.getName() + ", " + valExpr + ")");
					});

				String cond =
					keys.headOpt().map(p -> "this." + p.getName() + ".eq(" + getValGetter(p, "_row") + ")").get();
				cond = cond + keys.tail().map(p -> ".and(this." + p.getName() + ".eq(" + getValGetter(p, "_row") + "))")
					.toString("");
				println(".where(" + cond + ")");
				//println(".mapResult(countRes ->");
				//println("countRes.verify(c -> c == 1,\"Expected 1 row updated, not \" + countRes.orElseThrow() + \" for \" + _row)");
				//println(".map(count -> _row)");
				println(".flatMap(count -> count == 0");
				println("\t? Result.empty()");
				println("\t: count == 1 ? Result.success(_row) : Result.failure(\"More than one record update: \" + count)");
				println(");");

			}
			be();
		}

		private String getValGetter(RProperty p, String valueName) {
			String getter = valueName + ".get" + UString.firstUpperCase(p.getName()) + "()";
			if(p.getValueType().isRequired() == false) {
				getter = getter + ".orElse(null)";
			}
			return getter;
		}

		/**
		 * Generate The DB class for this Substema.<br>
		 *
		 * @param valueClasses All the tables value classes
		 *
		 * @return The Generated Db java file
		 */
		public Result<GeneratedJava> generateDb(PList<RValueClass> valueClasses) {
			return Result.function(valueClasses.map(vc -> vc.getTypeSig())).code(log -> {
				String dbName =
					atUtils.getOneAnnotation(substema.getPackageDef().getAnnotations(), rclassDbName)
						.map(at -> atUtils.getStringProperty(at, "name").orElse(""))
						.map(name -> "Db" + name)
						.orElse("Db");
				RClass dbCls = new RClass(packageName, dbName);
				addImport(dbCls);

				addImport(TransactionRunner.class);
				addImport(DbType.class);
				String schemaName =
					atUtils.getOneAnnotation(substema.getPackageDef().getAnnotations(), rclassSchema)
						.map(at -> atUtils.getStringProperty(at, "name").map(n -> "\"" + n + "\"").orElse(null))
						.orElse(null);
				generateJavaDoc(substema.getPackageDef().getAnnotations());
				bs("public class " + dbName);
				{


					valueClasses.forEach(vc -> {
						RClass cls     = vc.getTypeSig().getName();
						RClass exprCls = toExprClass(cls);
						addImport(exprCls);
						boolean isTableClass = atUtils.hasAnnotation(vc.getAnnotations(), rclassTable);
						if(isTableClass) {
							println("/**");
							println(" * Create a new database Table or View instance");
							println(" * @see " + exprCls.getFullName());
							println(" * @see " + cls.getFullName());
							println(" */");
							println("public static " + JavaGenUtils.toString(packageName, exprCls) + " " + UString
								.firstLowerCase(cls.getClassName()) + "(){ return new " + JavaGenUtils
								.toString(packageName, exprCls) + "(); }");
						}
						else {
							generateJavaDoc(vc.getAnnotations());
							println("public " + JavaGenUtils.toString(packageName, exprCls) + " " + UString
								.firstLowerCase(cls.getClassName()) + "(){ return new " + JavaGenUtils
								.toString(packageName, exprCls) + "(); }");
						}

					});

				}
				be();
				return toGenJava(dbCls);
			});

		}

		private RClass toExprClass(RClass cls) {
			return cls.withClassName("_" + cls.getClassName()).withPackageName(packageName);
		}

		/**
		 * Generate the java code for a property of a db table description value class;
		 *
		 * @param property The property to generate
		 */
		private void generateProperty(RProperty property) {
			Function<String, String> columnNameConverter =
				createSubstemaToDbNameConverter(substema.getPackageDef().getAnnotations(), NameType.column, atUtils);
			String type;
			String value;
			RClass cls        = property.getValueType().getTypeSig().getName();
			String columnName = property.getName();
			RAnnotation columnAt =
				atUtils.getOneAnnotation(property.getAnnotations(), rclassColumn).orElse(null);
			if(columnAt != null) {
				columnName = atUtils.getStringProperty(columnAt, "name").orElse(columnName);
			}
			else {
				columnName = columnNameConverter.apply(columnName);
			}
			generateJavaDoc(property.getAnnotations());

			if(SubstemaUtils.isNumberClass(cls)) {
				addImport(ETypeNumber.class);
				addImport(ExprPropertyNumber.class);
				type = "ETypeNumber<" + cls.getClassName() + ">";
				value = "new ExprPropertyNumber<>(" + cls.getClassName() + ".class,this,\"" + property
					.getName() + "\", \"" + columnName + "\");";
			}
			else if(cls.equals(SubstemaUtils.booleanRClass)) {
				addImport(ETypeBoolean.class);
				addImport(ExprPropertyBoolean.class);
				type = "ETypeBoolean";
				value = "new ExprPropertyBoolean(this,\"" + property.getName() + "\", \"" + columnName + "\");";
			}
			else if(cls.equals(SubstemaUtils.stringRClass)) {
				addImport(ETypeString.class);
				addImport(ExprPropertyString.class);
				type = "ETypeString";
				value = "new ExprPropertyString(this,\"" + property.getName() + "\", \"" + columnName + "\");";
			}
			else if(cls.equals(SubstemaUtils.dateTimeRClass)) {
				addImport(LocalDateTime.class);
				addImport(ExprPropertyDateTime.class);
				addImport(ETypeDateTime.class);
				type = "ETypeDateTime";
				value = "new ExprPropertyDateTime(this,\"" + property.getName() + "\", \"" + columnName + "\");";

			}
			else if(cls.equals(SubstemaUtils.dateRClass)) {
				addImport(LocalDate.class);
				addImport(ExprPropertyDate.class);
				addImport(ETypeDate.class);
				type = "ETypeDate";
				value = "new ExprPropertyDate(this,\"" + property.getName() + "\", \"" + columnName + "\");";

			}
			else if(cls.equals(SubstemaUtils.binaryRClass)){
				addImport(PByteList.class);
				addImport(Expr.class);
				addImport(ExprProperty.class);
				type = "Expr<PByteList>";
				value = "new ExprProperty<PByteList>(PByteList.class,this,\"" + property.getName() + "\", \"" + columnName + "\");";
			}
			else {
				if(cls.getPackageName().isEmpty()) {
					throw new DbJavaGenException("Unknown internal type:" + cls);
				}

				boolean isEnum = getInternalOrExternalEnum(cls).isPresent();

				if(isEnum) {
					addImport(cls);
					addImport(ExprPropertyEnum.class);
					String valueName = ExprPropertyEnum.class.getSimpleName();

					type = valueName + "<" + cls.getClassName() + ">";
					value = "new " + type + "(" + cls.getClassName() + ".class,this,\"" + property
						.getName() + "\", \"" + columnName + "\");";
				}
				else {
					RClass nc = toExprClass(cls);
					//addImport(cls);
					addImport(nc);
					addImport(ExprProperty.class);
					type = JavaGenUtils.toString(packageName, nc);
					String valueClass = cls.getClassName();
					//Check if we have a @NoPrefix on the property.
					RAnnotation noPrefixAt = atUtils.getOneAnnotation(property.getAnnotations(), rclassNoPrefix)
						.orElseGet(() ->
									   atUtils.getOneAnnotation(findValueClass(cls).getAnnotations(), rclassNoPrefix)
										   .orElse(null)
						);

					if(noPrefixAt != null) {
						columnName = "";
					}

					value = "new " + type + "(new ExprProperty(" + valueClass + ".class,this,\"" + property
						.getName() + "\", \"" + columnName + "\"));";
				}


			}
			println("public " + type + " " + property.getName() + " = " + value);
		}

		/**
		 * Try to find an enum for a given RClass.<br>
		 * if the RClass is for a different package, than that package is resolved.
		 *
		 * @param cls the RClass for the enum.
		 *
		 * @return the Optional REnum for the class
		 */
		private Optional<REnum> getInternalOrExternalEnum(RClass cls) {
			if(cls.getPackageName().equals(packageName) == false) {
				RSubstema ss = compiler.compile(cls.getPackageName()).orElseThrow();
				return ss.getEnums().find(e -> e.getName().getClassName().equals(cls.getClassName()));
			}
			else {
				return getEnum(cls);
			}
		}

		/**
		 * Find a  value class internally or externally...
		 *
		 * @param cls the RClass to find
		 *
		 * @return The Optional cls
		 */
		private RValueClass findValueClass(RClass cls) {
			if(cls.getPackageName().equals(substema.getPackageName())) {
				return getValueClass(cls)
					.orElseThrow(() -> new PersistSqlException("Can't find Internal Value class " + cls));
			}
			RSubstema ns = compiler.compile(cls.getPackageName()).orElseThrow();
			return ns.getValueClasses().find(vc -> vc.getTypeSig().getName().equals(cls))
				.orElseThrow(() -> new PersistSqlException("Can't find Value class " + cls));
		}

		/**
		 * Return the optional REnum defined in the substema substema by checking the RClass
		 *
		 * @param cls The RClass to find
		 *
		 * @return empty or some REnum
		 */
		private Optional<REnum> getEnum(RClass cls) {
			return substema.getEnums().find(vc -> vc.getName().equals(cls));
		}

		private String toString(RTypeSig sig) {
			return toString(sig, false);
		}

		private String toString(RTypeSig sig, boolean asPrimitive) {
			String gen =
				sig.getGenerics().isEmpty() ? "" : sig.getGenerics().map(this::toString).toString("<", ",", ">");
			String packName = sig.getName().getPackageName();
			String name  = sig.getName().getClassName();

			switch(name) {
				case "List":
					name = "PList";
					addImport(PList.class);
					break;
				case "Set":
					name = "PSet";
					addImport(PSet.class);
					break;
				case "Map":
					name = "PMap";
					addImport(PMap.class);
					break;

				case "Boolean":
					name = asPrimitive ? "boolean" : name;
					break;
				case "Byte":
					name = asPrimitive ? "byte" : name;
					break;
				case "Short":
					name = asPrimitive ? "short" : name;
					break;
				case "Integer":
					name = asPrimitive ? "int" : name;
					break;
				case "Long":
					name = asPrimitive ? "long" : name;
					break;
				case "Float":
					name = asPrimitive ? "float" : name;
					break;
				case "Double":
					name = asPrimitive ? "double" : name;
					break;

				case "String":
					break;

				default:
					addImport(new RClass(packName, name));
					break;
			}

			return name + gen;
		}


	}
}
