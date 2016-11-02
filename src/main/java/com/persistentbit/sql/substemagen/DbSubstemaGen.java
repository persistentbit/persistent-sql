package com.persistentbit.sql.substemagen;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.exceptions.RtSqlException;
import com.persistentbit.core.logging.PLog;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.utils.StringUtils;
import com.persistentbit.core.utils.ToDo;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.staticsql.codegen.DbAnnotationsUtils;
import com.persistentbit.substema.compiler.AnnotationsUtils;
import com.persistentbit.substema.compiler.SubstemaCompiler;
import com.persistentbit.substema.compiler.SubstemaUtils;
import com.persistentbit.substema.compiler.values.*;
import com.persistentbit.substema.compiler.values.expr.RConst;
import com.persistentbit.substema.compiler.values.expr.RConstString;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Substema generator for existing database tables
 *
 * @author petermuys
 * @since 1/11/16
 */
public class DbSubstemaGen{

	public static final PLog log = PLog.get(DbSubstemaGen.class);
	private final Supplier<Connection>     connectionSupplier;
	private final RSubstema                baseSubstema;
	private final String                   catalogName;
	private final String                   schemaName;
	private final SubstemaCompiler         substemaCompiler;
	private final AnnotationsUtils         atUtils;
	private final Function<String, String> mapColumnNameToSubstemaName;
	private final Function<String, String> mapTableNameToSubstemaName;

	private final Function<String, String> mapSubstemaTableNameToDbName;
	private final Function<String, String> mapSubstemaColumnNameToDbName;

	private PList<RValueClass> valueClasses = PList.empty();

	public DbSubstemaGen(Supplier<Connection> connectionSupplier, RSubstema baseSubstema,
						 SubstemaCompiler substemaCompiler,
						 String catalogName, String schemaName
	) {
		this.connectionSupplier = connectionSupplier;
		this.baseSubstema = baseSubstema;
		this.substemaCompiler = substemaCompiler;
		this.atUtils = new AnnotationsUtils(substemaCompiler);
		this.catalogName = catalogName;
		this.schemaName = schemaName;
		this.mapColumnNameToSubstemaName = DbAnnotationsUtils
			.createDbNameToSubstemaNameConverter(
				baseSubstema.getPackageDef().getAnnotations(), DbAnnotationsUtils.NameType.column, atUtils);
		this.mapTableNameToSubstemaName = DbAnnotationsUtils
			.createDbNameToSubstemaNameConverter(
				baseSubstema.getPackageDef().getAnnotations(), DbAnnotationsUtils.NameType.table, atUtils);
		this.mapSubstemaTableNameToDbName = DbAnnotationsUtils
			.createSubstemaToDbNameConverter(
				baseSubstema.getPackageDef().getAnnotations(), DbAnnotationsUtils.NameType.table, atUtils);
		this.mapSubstemaColumnNameToDbName = DbAnnotationsUtils
			.createSubstemaToDbNameConverter(
				baseSubstema.getPackageDef().getAnnotations(), DbAnnotationsUtils.NameType.column, atUtils);
	}


	/**
	 * Load the definition of all the tables (matching "%").
	 *
	 * @see #loadTables(String)
	 */
	public void loadTables() {
		loadTables("%");
	}

	/**
	 * Load the definition of all the tables matching the supplied table name pattern.<br>
	 *
	 * @param pattern The pattern for the table name.  Use '%' to match all
	 */
	public void loadTables(String pattern) {
		List<String> tableNames = new ArrayList<>();
		try(Connection c = connectionSupplier.get()) {
			DatabaseMetaData md = c.getMetaData();
			ResultSet        rs = md.getTables(catalogName, schemaName, pattern, new String[]{"TABLE", "VIEW"});
			while(rs.next()) {
				tableNames.add(rs.getString("TABLE_NAME"));
			}
			rs.close();
		} catch(SQLException e) {
			RtSqlException.map(e);
		}
		for(String name : tableNames) {
			valueClasses = valueClasses.plus(table(name));
		}

	}

	private RValueClass table(String tableName) {
		try(Connection c = connectionSupplier.get()) {
			DatabaseMetaData md = c.getMetaData();

			//GET PRIMARY KEYS FOR TABLE

			PMap<String, Integer> primKeys = PMap.empty();
			try(ResultSet rs = md.getPrimaryKeys(catalogName, schemaName, tableName)) {
				while(rs.next()) {
					String name   = rs.getString("COLUMN_NAME");
					int    keySeq = rs.getInt("KEY_SEQ");
					primKeys = primKeys.put(name, keySeq);
				}
			}


			PList<RProperty> properties  = PList.empty();
			String           packageName = baseSubstema.getPackageName();

			//GET COLUMNS
			try(ResultSet rs = md.getColumns(catalogName, schemaName, tableName, "%")) {
				while(rs.next()) {

					String  name            = rs.getString("COLUMN_NAME");
					int     data_type       = rs.getInt("DATA_TYPE"); //java.sql.Types
					String  typeName        = rs.getString("TYPE_NAME"); //datasource dependent
					int     columnSize      = rs.getInt("COLUMN_SIZE");
					Integer decimalDigits   = (Integer) rs.getObject("DECIMAL_DIGITS");
					String  defaultValue    = rs.getString("COLUMN_DEF");
					int     ordinalPos      = rs.getInt("ORDINAL_POSITION");
					int     charOctetLength = rs.getInt("CHAR_OCTET_LENGTH");
					boolean isNullable      = "YES".equals(rs.getString("IS_NULLABLE"));
					String  scopeTable      = rs.getString("SCOPE_TABLE");
					Object  scopeDataType   = rs.getObject("SOURCE_DATA_TYPE");
					boolean isAutoIncrement = "YES".equals(rs.getString("IS_AUTOINCREMENT"));
					Integer decimal_digits  = rs.getInt("DECIMAL_DIGITS");
					String  remarks         = rs.getString("REMARKS");
					SqlType sqlType;
					try {
						sqlType = SqlType.fromJavaSqlType(data_type);

					} catch(Exception e) {
						throw new RuntimeException("Error getting sql type for column " + name, e);
					}
					boolean isKey = primKeys.getOpt(name).isPresent();

					if(sqlType.getTypeSig().isPresent()) {
						RValueType valueType =
							new RValueType(sqlType.getTypeSig().get(), isNullable == false);

						PList<RAnnotation> propAnnotations = PList.empty();
						if(isKey) {
							propAnnotations =
								propAnnotations.plus(new RAnnotation(DbAnnotationsUtils.rclassKey, PMap.empty()));
						}
						if(isAutoIncrement) {
							propAnnotations =
								propAnnotations.plus(new RAnnotation(DbAnnotationsUtils.rclassAutoGen, PMap.empty()));
						}
						String propName = mapColumnNameToSubstemaName.apply(name);
						if(mapSubstemaColumnNameToDbName.apply(propName).equals(name) == false) {
							PMap<String, RConst> nameValue = PMap.empty();
							nameValue = nameValue.put("name", new RConstString(name));
							propAnnotations =
								propAnnotations.plus(new RAnnotation(DbAnnotationsUtils.rclassColumn, nameValue));
						}
						RProperty prop = new RProperty(
							propName, valueType, propAnnotations);

						properties = properties.plus(prop);
					}
					else {
						//TODO find a better solution
						log.error("Can't convert " + sqlType + " in " + tableName + "." + name + ": SKIPPING COLUMN");
					}
				}
			}

			PList<RAnnotation> annotations = PList.empty();

			String reverseTableName = mapTableNameToSubstemaName.apply(tableName);
			if(mapSubstemaTableNameToDbName.apply(reverseTableName).equals(tableName) == false) {
				PMap<String, RConst> nameValue = PMap.empty();
				nameValue = nameValue.put("name", new RConstString(tableName));
				annotations = annotations.plus(new RAnnotation(DbAnnotationsUtils.rclassTable, nameValue));
			}
			else {
				annotations = annotations.plus(new RAnnotation(DbAnnotationsUtils.rclassTable, PMap.empty()));
			}


			RValueClass vc =
				new RValueClass(
					new RTypeSig(new RClass(packageName, reverseTableName)),
					properties,
					PList.empty(), //interfaceClasses
					annotations
				);
			return vc;

		} catch(SQLException se) {
			RtSqlException.map(se);
			return null;
		}
	}

	/**
	 * Merges the resulting value classes by
	 * removing all value classes that already exist in the baseSubstema.<br>
	 * The matching with the baseSubstema value classes are based on the resulting database table name.
	 */
	public void mergeWithBase() {
		valueClasses =
			valueClasses.filter(vc -> baseSubstemaHasMatchingTable(vc.getTypeSig().getName().getClassName()) == false);
	}

	private boolean baseSubstemaHasMatchingTable(String valueClassName) {
		String tableName = mapSubstemaTableNameToDbName.apply(valueClassName);
		return baseSubstema.getValueClasses().find(vc -> {
			RAnnotation tableAnnotation =
				vc.getAnnotations().find(at -> at.getName().getClassName().equals("Table")).orElse(null);

			if(tableAnnotation == null) {
				return false;
			}
			String definedName = atUtils.getStringProperty(tableAnnotation, "name").orElse(null);
			if(definedName != null) {
				return "tableName".equals(definedName);
			}
			String vcTableName = mapSubstemaTableNameToDbName.apply(vc.getTypeSig().getName().getClassName());
			return tableName.equals(vcTableName);
		}).isPresent();
	}

	/**
	 * Try to use an embeddable class in generated state classes
	 *
	 * @param packageName The packageName of the embedded class
	 * @param className   The className of the embedded class
	 */
	public void mergeEmbedded(String packageName, String className) {
		RClass cls = new RClass(packageName, className);

		//Let's first check if we have an import for the packageName...
		if(baseSubstema.getImports().find(i -> i.getPackageName().equals(packageName)).isPresent() == false &&
			baseSubstema.getPackageName().equals(packageName) == false) {

			throw new PersistSqlException("Can't merge " + cls.getFullName() + " into " +
											  baseSubstema.getPackageName() + " because the package is not imported.");
		}

		RValueClass embeddable = findValueClass(cls)
			.orElseThrow(() -> new PersistSqlException("Can't find " + cls.getFullName()));
		mergeEmbedded(embeddable);
	}

	private Optional<RValueClass> findValueClass(RClass cls) {
		RSubstema substema = substemaCompiler.compile(cls.getPackageName());
		return substema.getValueClasses().find(vc -> vc.getTypeSig().getName().equals(cls));
	}

	private Optional<REnum> findEnumClass(RClass cls) {
		RSubstema substema = substemaCompiler.compile(cls.getPackageName());
		return substema.getEnums().find(e -> e.getName().equals(cls));
	}

	private void mergeEmbedded(RValueClass embeddable) {
		valueClasses = valueClasses.map(gen -> mergeEmbedded(gen, embeddable));
	}

	private RValueClass mergeEmbedded(RValueClass generated, RValueClass embeddable) {
		//ToDo implement this
		PList<Tuple2<String, RValueType>> embCols = getFullColumnList(embeddable);

		//embCols.forEach(System.out::println);
		//System.out.println();

		PList<Tuple2<String, RValueType>> genCols = getFullColumnList(generated);
		genCols.forEach(System.out::println);
		//System.out.println();

		PMap<String, PList<String>> found = PMap.empty();    //key = prefix name value = embedded columnName
		for(Tuple2<String, RValueType> genCol : genCols) {
			String genColName = genCol._1;

			Tuple2<String, RValueType> matchingEmbCol = embCols.find(t -> genColName.endsWith(t._1)).orElse(null);
			if(matchingEmbCol != null) {
				String prefix = genColName.substring(0, genColName.length() - matchingEmbCol._1.length());

				found = found.put(prefix, found.getOpt(prefix).orElse(PList.empty()).plus(matchingEmbCol._1));
			}

		}
		//Filter found to get all the prefixes that have a complete embedded object
		found = found.filter(t -> t._2.containsAll(embCols.map(te -> te._1)));


		//For each prefix found.
		for(Tuple2<String, PList<String>> item : found) {
			String prefix = item._1;
			//Remove all involved properties for this prefix
			boolean areAllFieldNullable = true;
			for(String postFix : item._2) {
				String           fullName         = prefix + postFix;
				PList<RProperty> allProps         = generated.getProperties();
				RProperty        matchingProperty = allProps.find(p -> getColumnNameForProperty(p).equals(fullName))
					.orElseThrow(() -> new PersistSqlException("Can't find " + fullName));
				if(matchingProperty.getDefaultValue().isPresent() || matchingProperty.getValueType().isRequired()) {
					areAllFieldNullable = false;
				}
				generated = generated.withProperties(allProps.filter(p -> p != matchingProperty));
			}
			//Now we need add the embedded object as a property
			PList<RAnnotation> annotations          = PList.empty();
			String             substamaPropertyName =
				prefix.isEmpty() ? "" : mapColumnNameToSubstemaName.apply(prefix.substring(0, prefix.length() - 1));
			if(substamaPropertyName.isEmpty()) {
				//If the prefix name is empty...
				substamaPropertyName = StringUtils.firstLowerCase(embeddable.getTypeSig().getName().getClassName());
				RAnnotation colAt = new RAnnotation(DbAnnotationsUtils.rclassColumn, PMap.<String, RConst>empty()
					.put("name", new RConstString("")));
				annotations = annotations.plus(colAt);
			}
			RProperty newProperty = new RProperty(substamaPropertyName, new RValueType(embeddable
																						   .getTypeSig(), areAllFieldNullable == false), annotations);
			generated = generated.withProperties(generated.getProperties().plus(newProperty));
		}


		//found.forEach(System.out::println);
		//System.out.println("---------");
		//System.out.println();

		return generated;
	}

	private PList<Tuple2<String, RValueType>> getFullColumnList(RValueClass vc) {
		PList<Tuple2<String, RValueType>> res = PList.empty();

		for(RProperty prop : vc.getProperties()) {

			RClass propClass = prop.getValueType().getTypeSig().getName();

			String columnNameForProp = getColumnNameForProperty(prop);

			if(SubstemaUtils.isSubstemaClass(propClass)) {
				//Substema class
				if(SubstemaUtils.isCollectionClass(propClass)) {
					//ToDo
					throw new ToDo("Not yet implemented: collections for Db Substema code generator");
				}
				res = res.plus(Tuple2.of(columnNameForProp, prop.getValueType()));
			}
			else {
				//Must be an enum or valueClass
				REnum e = findEnumClass(prop.getValueType().getTypeSig().getName()).orElse(null);
				if(e != null) {
					res = res.plus(Tuple2.of(columnNameForProp, prop.getValueType()));
				}
				else {
					//Must be a valueClass
					res = res.plusAll(getFullColumnList(findValueClass(propClass).get())
										  .map(t -> t.with_1(columnNameForProp + "_" + t._1)));
				}

			}
		}
		return res;
	}

	private String getColumnNameForProperty(RProperty prop) {
		RAnnotation columnAnnotation =
			atUtils.getOneAnnotation(prop.getAnnotations(), DbAnnotationsUtils.rclassColumn).orElse(null);
		if(columnAnnotation != null) {
			String name = atUtils.getStringProperty(columnAnnotation, "name").orElse(null);
			if(name != null) {
				return name;
			}
		}
		return mapSubstemaColumnNameToDbName.apply(prop.getName());
	}

	public PList<RValueClass> getValueClasses() {
		return valueClasses;
	}

}
