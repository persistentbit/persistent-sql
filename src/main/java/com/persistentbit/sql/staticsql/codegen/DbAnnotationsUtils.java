package com.persistentbit.sql.staticsql.codegen;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PSet;
import com.persistentbit.core.utils.StringUtils;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.substema.compiler.AnnotationsUtils;
import com.persistentbit.substema.compiler.values.RAnnotation;
import com.persistentbit.substema.compiler.values.RClass;
import com.persistentbit.substema.compiler.values.expr.RConstEnum;

import java.util.function.Function;

/**
 * Utility values en functions for working with Substema Database Annotations
 *
 * @author petermuys
 * @since 1/11/16
 */
public final class DbAnnotationsUtils{


	public static final String packageDbAnnotations = "com.persistentbit.sql.annotations";

	public static final RClass rclassDbName  = new RClass(packageDbAnnotations, "DbName");
	public static final RClass rclassSchema  = new RClass(packageDbAnnotations, "Schema");
	public static final RClass rclassTable   = new RClass(packageDbAnnotations, "Table");
	public static final RClass rclassColumn  = new RClass(packageDbAnnotations, "Column");
	public static final RClass rclassAutoGen = new RClass(packageDbAnnotations, "AutoGen");
	public static final RClass rclassKey     = new RClass(packageDbAnnotations, "Key");

	public static final RClass rclassNameToLower      = new RClass(packageDbAnnotations, "NameToLower");
	public static final RClass rclassNameToUpper      = new RClass(packageDbAnnotations, "NameToUpper");
	public static final RClass rclassCamelToSnake     = new RClass(packageDbAnnotations, "NameCamelToSnake");
	public static final RClass rclassNameRemovePrefix = new RClass(packageDbAnnotations, "NameRemovePrefix");
	public static final RClass rclassPrefix           = new RClass(packageDbAnnotations, "NamePrefix");
	public static final RClass rclassPostfix          = new RClass(packageDbAnnotations, "NamePostfix");
	public static final RClass rclassNoPrefix         = new RClass(packageDbAnnotations, "NoPrefix");

	//DbImport

	public static final RClass rclassDbImportExcludeTables         =
		new RClass(packageDbAnnotations, "DbImportExcludeTables");
	public static final RClass rclassDbImportIncludeTables         =
		new RClass(packageDbAnnotations, "DbImportIncludeTables");
	public static final RClass rclassDbImportExcludeColumns        =
		new RClass(packageDbAnnotations, "DbImportExcludeColumns");
	public static final RClass rclassDbImportMergeEmbedded         =
		new RClass(packageDbAnnotations, "DbImportMergeEmbedded");
	public static final RClass rclassDbImportSetTableSubstemaName  =
		new RClass(packageDbAnnotations, "DbImportSetTableSubstemaName");
	public static final RClass rclassDbImportSetColumnSubstemaName =
		new RClass(packageDbAnnotations, "DbImportSetColumnSubstemaName");
	public static final RClass rclassDbImportUseEnum               =
		new RClass(packageDbAnnotations, "DbImportUseEnum");


	public static final PSet<RClass> nameConverterAnnotations =
		PSet.val(rclassNameToLower, rclassNameToUpper, rclassCamelToSnake, rclassNameRemovePrefix,
				 rclassPrefix, rclassPostfix, rclassNoPrefix
		);

	/**
	 * Check if this is a name converter annotation
	 *
	 * @param at The annotation to check
	 *
	 * @return true if it is
	 */
	public static boolean isNameConverterAnnotation(RAnnotation at) {
		return nameConverterAnnotations.find(nc -> nc.equals(at.getName())).isPresent();
	}

	/**
	 * Create a function that can convert a substema class or property name to a database table or column name.
	 *
	 * @param annotations The list of conversion annotations
	 * @param type        The type of name to convert
	 * @param atUtils     Annotations utilities
	 *
	 * @return A new function that generates a substema name from a database name
	 *
	 * @see #createDbNameToSubstemaNameConverter(PList, NameType, AnnotationsUtils)
	 */
	public static Function<String, String> createSubstemaToDbNameConverter(
		PList<RAnnotation> annotations, NameType type,
		AnnotationsUtils atUtils
	) {
		//Find all annotations
		PList<RAnnotation> nameAt = annotations.filter(DbAnnotationsUtils::isNameConverterAnnotation);

		Function<String, String> res = i -> i;
		for(RAnnotation at : nameAt) {
			RConstEnum constEnum  = (RConstEnum) atUtils.getProperty(at, "type").get();
			String     atTypeName = constEnum.getEnumValue();

			boolean ok = (type == NameType.table && atTypeName.equals("table")) ||
				(type == NameType.column && atTypeName.equals("column")) ||
				atTypeName.equals("all");
			if(ok == false) {
				continue;
			}
			switch(at.getName().getClassName()) {
				case "NameToLower":
					res = res.andThen(String::toLowerCase);
					break;
				case "NameToUpper":
					res = res.andThen(String::toUpperCase);
					break;
				case "NameCamelToSnake":
					res = res.andThen(StringUtils::camelCaseTo_snake);
					break;
				case "NamePrefix":
					String prefix = atUtils.getStringProperty(at, "value").orElse(null);
					res = res.andThen(s -> prefix + s);
					break;
				case "NamePostfix":
					String postFix = atUtils.getStringProperty(at, "value").orElse(null);
					res = res.andThen(s -> s + postFix);
					break;
				case "NameRemovePrefix":
					res = res.andThen(s -> {
						String delPrefix = atUtils.getStringProperty(at, "value").orElse(null);
						if(s.startsWith(delPrefix)) {
							return s.substring(delPrefix.length());
						}
						return s;
					});
					break;
				default:
					throw new PersistSqlException("Unknown:" + constEnum.getEnumClass());
			}
		}
		return res;
	}

	/**
	 * Create a function that can convert a database table or column name to a substema class or property name.<br>
	 * This is the reverse conversion from createSubstemaToDbNameConverter.<br>
	 *
	 * @param annotations The list of conversion annotations
	 * @param type        The type of name to convert
	 * @param atUtils     Annotations utilities
	 *
	 * @return A new function that generates a substema name from a database name
	 *
	 * @see #createSubstemaToDbNameConverter(PList, NameType, AnnotationsUtils)
	 */
	public static Function<String, String> createDbNameToSubstemaNameConverter(
		PList<RAnnotation> annotations, NameType type,
		AnnotationsUtils atUtils
	) {
		//Find all annotations
		PList<RAnnotation> nameAt = annotations.filter(DbAnnotationsUtils::isNameConverterAnnotation);

		Function<String, String> res = i -> i;

		PList<RAnnotation> sortedAt = nameAt.reversed();

		//Lets change the order:
		// First NameToUpper => Gives a lower case name.
		// Then NameCamelToSnake => gives a camelCase name

		PList<RAnnotation> prefixAt = sortedAt.filter(at -> at.getName().getClassName().equals("NamePrefix"));
		for(RAnnotation at : prefixAt) {
			String     prefix     = atUtils.getStringProperty(at, "value").orElse(null);
			RConstEnum constEnum  = (RConstEnum) atUtils.getProperty(at, "type").get();
			String     atTypeName = constEnum.getEnumValue();
			boolean ok = (type == NameType.table && atTypeName.equals("table")) ||
				(type == NameType.column && atTypeName.equals("column")) ||
				atTypeName.equals("all");
			if(ok) {
				res = res.andThen(s -> s.startsWith(prefix) ? s.substring(prefix.length()) : s);
			}
		}

		if(sortedAt.find(at -> at.getName().getClassName().equals("NameToUpper")).isPresent()) {
			res = res.andThen(String::toLowerCase);
		}
		if(sortedAt.find(at -> at.getName().getClassName().equals("NameCamelToSnake")).isPresent()) {
			res = res.andThen(StringUtils::snake_toCamelCase);
		}

		for(RAnnotation at : sortedAt) {
			RConstEnum constEnum  = (RConstEnum) atUtils.getProperty(at, "type").get();
			String     atTypeName = constEnum.getEnumValue();

			boolean ok = (type == NameType.table && atTypeName.equals("table")) ||
				(type == NameType.column && atTypeName.equals("column")) ||
				atTypeName.equals("all");
			if(ok == false) {
				continue;
			}


			switch(at.getName().getClassName()) {
				case "NameToLower":
					//Substema name should not be uppercase -> we do nothing
					break;
				case "NameToUpper":
					//Already done before
					break;
				case "NameCamelToSnake":
					//Already done before
					break;
				case "NamePrefix":
					//String prefix = atUtils.getStringProperty(at, "value").orElse(null);
					//res = res.andThen(s -> s.startsWith(prefix) ? s.substring(prefix.length()) : s);
					//Already done before
					break;
				case "NamePostfix":
					String postFix = atUtils.getStringProperty(at, "value").orElse(null);
					res = res.andThen(s -> s.endsWith(postFix) ? s.substring(0, s.length() - postFix.length()) : s);
					break;
				case "NameRemovePrefix":
					res = res.andThen(s -> {
						String delPrefix = atUtils.getStringProperty(at, "value").orElse(null);
						s = delPrefix + StringUtils.firstUpperCase(s);
						return s;
					});
					break;
				default:
					throw new PersistSqlException("Unknown:" + constEnum.getEnumClass());
			}
		}
		if(type == NameType.table) {
			//Make sure we start the name with an uppercase.
			res = res.andThen(StringUtils::firstUpperCase);
		}
		return res;
	}

	public enum NameType{
		column, table
	}
}
