package com.persistentbit.sql.substemagen;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.exceptions.RtSqlException;
import com.persistentbit.core.logging.PLog;
import com.persistentbit.sql.staticsql.codegen.DbAnnotationsUtils;
import com.persistentbit.substema.compiler.AnnotationsUtils;
import com.persistentbit.substema.compiler.SubstemaCompiler;
import com.persistentbit.substema.compiler.values.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Substema generator for existing database tables
 *
 * @author petermuys
 * @since 1/11/16
 */
public class DbSubstemaGen {
    public static final PLog log = PLog.get(DbSubstemaGen.class);
    private final Supplier<Connection> connectionSupplier;
    private final RSubstema baseSubstema;
    private final String catalogName;
    private final String schemaName;
    private final SubstemaCompiler substemaCompiler;
    private final AnnotationsUtils atUtils;
    private final Function<String, String> mapColumnNameToSubstemaName;
    private final Function<String, String> mapTableNameToSubstemaName;

    private final Function<String, String> mapSubstemaTableNameToDbName;

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
        try (Connection c = connectionSupplier.get()) {
            DatabaseMetaData md = c.getMetaData();
            ResultSet rs = md.getTables(catalogName, schemaName, pattern, new String[]{"TABLE", "VIEW"});
            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
            rs.close();
        } catch (SQLException e) {
            RtSqlException.map(e);
        }
        for (String name : tableNames) {
            valueClasses = valueClasses.plus(table(name));
        }

    }

    private RValueClass table(String tableName) {
        try (Connection c = connectionSupplier.get()) {
            DatabaseMetaData md = c.getMetaData();

            //GET PRIMARY KEYS FOR TABLE

            PMap<String, Integer> primKeys = PMap.empty();
            try (ResultSet rs = md.getPrimaryKeys(catalogName, schemaName, tableName)) {
                while (rs.next()) {
                    String name = rs.getString("COLUMN_NAME");
                    int keySeq = rs.getInt("KEY_SEQ");
                    primKeys = primKeys.put(name, keySeq);
                }
            }


            PList<RProperty> properties = PList.empty();
            String packageName = baseSubstema.getPackageName();

            //GET COLUMNS
            try (ResultSet rs = md.getColumns(catalogName, schemaName, tableName, "%")) {
                while (rs.next()) {

                    String name = rs.getString("COLUMN_NAME");
                    int data_type = rs.getInt("DATA_TYPE"); //java.sql.Types
                    String typeName = rs.getString("TYPE_NAME"); //datasource dependent
                    int columnSize = rs.getInt("COLUMN_SIZE");
                    Integer decimalDigits = (Integer) rs.getObject("DECIMAL_DIGITS");
                    String defaultValue = rs.getString("COLUMN_DEF");
                    int ordinalPos = rs.getInt("ORDINAL_POSITION");
                    int charOctetLength = rs.getInt("CHAR_OCTET_LENGTH");
                    boolean isNullable = "YES".equals(rs.getString("IS_NULLABLE"));
                    String scopeTable = rs.getString("SCOPE_TABLE");
                    Object scopeDataType = rs.getObject("SOURCE_DATA_TYPE");
                    boolean isAutoIncrement = "YES".equals(rs.getString("IS_AUTOINCREMENT"));
                    Integer decimal_digits = rs.getInt("DECIMAL_DIGITS");
                    String remarks = rs.getString("REMARKS");
                    SqlType sqlType;
                    try {
                        sqlType = SqlType.fromJavaSqlType(data_type);

                    } catch (Exception e) {
                        throw new RuntimeException("Error getting sql type for column " + name, e);
                    }
                    boolean isKey = primKeys.getOpt(name).isPresent();

                    if (sqlType.getTypeSig().isPresent()) {
                        RValueType valueType =
                                new RValueType(sqlType.getTypeSig().get(), isNullable == false);

                        PList<RAnnotation> propAnnotations = PList.empty();
                        if (isKey) {
                            propAnnotations =
                                    propAnnotations.plus(new RAnnotation(DbAnnotationsUtils.rclassKey, PMap.empty()));
                        }
                        if (isAutoIncrement) {
                            propAnnotations =
                                    propAnnotations.plus(new RAnnotation(DbAnnotationsUtils.rclassAutoGen, PMap.empty()));
                        }

                        RProperty prop = new RProperty(
                                mapColumnNameToSubstemaName.apply(name), valueType, propAnnotations);
                        properties = properties.plus(prop);
                    } else {
                        //TODO find a better solution
                        log.error("Can't convert " + sqlType + " in " + tableName + "." + name + ": SKIPPING COLUMN");
                    }
                }
            }

            PList<RAnnotation> annotations = PList.empty();
            annotations = annotations.plus(new RAnnotation(DbAnnotationsUtils.rclassTable, PMap.empty()));
            RValueClass vc =
                    new RValueClass(
                            new RTypeSig(new RClass(packageName, mapTableNameToSubstemaName.apply(tableName))),
                            properties,
                            PList.empty(), //interfaceClasses
                            annotations
                    );
            return vc;

        } catch (SQLException se) {
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

            if (tableAnnotation == null) {
                return false;
            }
            String definedName = atUtils.getStringProperty(tableAnnotation, "name").orElse(null);
            if (definedName != null) {
                return "tableName".equals(definedName);
            }
            String vcTableName = mapSubstemaTableNameToDbName.apply(vc.getTypeSig().getName().getClassName());
            return tableName.equals(vcTableName);
        }).isPresent();
    }

    public PList<RValueClass> getValueClasses() {
        return valueClasses;
    }

}
