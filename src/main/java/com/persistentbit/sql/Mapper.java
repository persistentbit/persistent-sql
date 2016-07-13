package com.persistentbit.sql;


import com.persistentbit.core.Lazy;
import com.persistentbit.core.NotNullable;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.lenses.Lens;
import com.persistentbit.core.utils.ImTools;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Peter Muys
 * @since 4/07/2016
 */
public class Mapper<S> implements Function<Record,S> {
    static private final ImTools<Mapper> im = ImTools.get(Mapper.class);
    static private class Col{

        final String name;
        final boolean isId;
        final boolean isAutoGen;
        public Col(String name, boolean isId, boolean isAutoGen)
        {
            this.name = name;
            this.isAutoGen = isAutoGen;
            this.isId = isId;
        }
        public Col asId(){
            return new Col(name,true,isAutoGen);
        }
        public Col asAutoGen() {
            return new Col(name,true,true);
        }
    }

    private final String tableName;
    private final PList<Col> cols;
    private final String prefix;
    private final Function<Record,S> fromRecord;
    private final BiFunction<S,SqlArguments<?>,S> toStat;
    transient @NotNullable
    private final Lazy<PList<Col>> ids;

    static public final Lens<Mapper, String> _prefix = im.lens("prefix");

    static public final Lens<Mapper, PList<Col>> _cols = im.lens("cols");
    static public final Lens<Mapper, Function<Record, ?>> _fromRecord = im.lens("fromRecord");
    static public final Lens<Mapper, BiFunction<?, SqlArguments<?>, ?>> _toStat = im.lens("toStat");


    public Mapper(String tableName, PList<Col> cols, String prefix, Function<Record,S> fromRecord, BiFunction<S,SqlArguments<?>,S> toStat){
        this.tableName = tableName;
        this.cols = cols;
        this.prefix = prefix;
        this.fromRecord = fromRecord;
        this.toStat  = toStat;
        this.ids = new Lazy<>(() ->cols.filter(v-> v.isId).plist());
    }
    public Mapper(String tableName, String prefix) {
        this(tableName, PList.empty(),prefix,null,null);
    }
    public Mapper(String tableName, String prefix, Transactions trans) {
        this(tableName,trans.run(()-> getFieldsFromDb(tableName,trans.get())),prefix,null,null);
    }

    public static PList<Col> getFieldsFromDb(String tableName, Connection c){
        Set<String> primKeys = new HashSet<>();
        PList<Col> res = PList.empty();
        try {
            DatabaseMetaData metaData = c.getMetaData();
            try(ResultSet rs = metaData.getPrimaryKeys(null,null,tableName)){
                while(rs.next()){
                    primKeys.add(rs.getString("COLUMN_NAME"));
                }
            }
            try(ResultSet rs = metaData.getColumns(null,null,tableName,"%")){
                while(rs.next())
                {

                    String name = rs.getString("COLUMN_NAME");
                    //int data_type = rs.getInt("DATA_TYPE"); //java.sql.Types
                    //String typeName = rs.getString("TYPE_NAME"); //datasource dependent
                    //int columnSize = rs.getInt("COLUMN_SIZE");
                    //Integer decimalDigits = (Integer) rs.getObject("DECIMAL_DIGITS");
                    //String defaultValue = rs.getString("COLUMN_DEF");
                    //int ordinalPos = rs.getInt("ORDINAL_POSITION");
                    //int charOctetLength = rs.getInt("CHAR_OCTET_LENGTH");
                    //String isNullable = rs.getString("IS_NULLABLE");
                    //String scopeTable = rs.getString("SCOPE_TABLE");
                    //Object scopeDataType = rs.getObject("SOURCE_DATA_TYPE");
                    boolean isAutoIncrement = rs.getString("IS_AUTOINCREMENT").equals("YES");
                    //Integer decimal_digits = rs.getInt("DECIMAL_DIGITS");
                    //String remarks = rs.getString("REMARKS");
                    //System.out.println(name + "," + isAutoIncrement);
                    res = res.plus(new Col(name,primKeys.contains(name),isAutoIncrement));
                }
            }

            return res;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }


    public S map(Record r){
        if(prefix != null){
            r = new RecordSubSet(prefix+"_",r);
        }
        return fromRecord.apply(r);
    }

    public Mapper<S> withFields(String...names){
        PList<Col> r = cols;
        for(String name :names){
            r = r.plus(new Col(name,false,false));
        }
        return _cols.set(this,r);
    }

    public Mapper<S> withIdFields(String...names){
        PList<Col> r = cols;
        for(String name :names){
            r = r.plus(new Col(name,true,false));
        }
        return _cols.set(this,r);
    }
    public Mapper<S> withAutoGenFields(String...names){
        PList<Col> r = cols;
        for(String name :names){
            r = r.plus(new Col(name,true,true));
        }
        return _cols.set(this,r);
    }

    public Mapper<S> withPrefix(String prefix){
        return _prefix.set(this,prefix);
    }

    public Mapper<S> withFromRecord(Function<Record,S> fromRecord){
        return (Mapper<S>)_fromRecord.set(this,fromRecord);
    }

    public Mapper<S> withToStat(BiFunction<S,SqlArguments<?>,S> toStat){
        return (Mapper<S>)_toStat.set(this,toStat);
    }

    @Override
    public S apply(Record record) {
        return map(record);
    }

    public Mapper<S> setStatArgs(S state, SqlArguments<?> stat){
        if(prefix != null){
            stat = new SqlArgumentsWithPrefix(prefix,stat);
        }
        toStat.apply(state,stat);
        return this;
    }
    public String createSelectList() {
        String res = "";
        for(Col c : cols){
            if(res.isEmpty() == false){
                res += ",";
            }
            if(prefix != null){
                res += prefix + "." + c.name + " as " + prefix+"_" + c.name;
            } else{
                res += c.name;
            }

        }
        return res;
    }


    public String createSelectById(){
        String tn = tableName;
        if(prefix != null){
            tn = tableName + " as " + prefix;
        }
        return "select " + createSelectList() + " from "+ tn  + " where " + createWherePartId();
    }

    public String createSelectAll() {
        String tn = tableName;
        if(prefix != null){
            tn = tableName + " as " + prefix;
        }
        return "select " + createSelectList() + " from "+ tn;
    }

    public String createInsert() {

        String names = "";
        String values = "";
        PList<Col> r = cols.filter(c -> c.isAutoGen == false);
        values = r.map(c -> ":" + undfix(c.name)).join((a,b)-> a + ", " + b).orElse("");
        names = r.map(c-> c.name).join((a,b)-> a+", " + b).orElse("");

        return "insert into " + tableName + " (" + names + ") values(" + values + ")";
    }

    public String createUpdateForId(){
        return createUpdate(createWherePartId());
    }

    public String createUpdate(String wherePart) {
        PList<Col> s = cols.filter(c -> c.isId == false);
        String r  = s.map(c -> dotfix(c.name))
                .map(n -> n + "=:" + n)
                .join((a,b) -> a + ", " + b).orElse("");

        String tn = prefix!=null ? tableName + " as " +  prefix : tableName;
        return "update " + tn + " set " + r + " where " + wherePart;
    }

    public String createWherePartId() {
        String s = ids.get().map(c -> dotfix(c.name) + "=:" + undfix(c.name)).join((a, b)-> a + " and " + b).orElse("");
        return s;
    }

    private String wprefix(String name, String sep){
        if(prefix == null){
            return name;
        }
        return prefix + sep + name;
    }
    private String dotfix(String name){
        return wprefix(name,".");
    }
    private String undfix(String name){
        return wprefix(name,"_");
    }

    public String createDeleteForId(){
        return createDelete(createWherePartId());
    }

    public String createDelete(String wherePart) {
        String tn = prefix!=null ? tableName + " as " +  prefix : tableName;
        return "delete " + tn + " where " + wherePart;
    }

    public Optional<String> getPrefix() {
        return Optional.ofNullable(prefix);
    }

    public Optional<Function<Record, S>> getFromRecord() {
        return Optional.ofNullable(fromRecord);
    }

    public Optional<BiFunction<S, SqlArguments<?>, S>> getToStat() {
        return Optional.ofNullable(toStat);
    }

    public String getTableName() {
        return tableName;
    }

    public PList<Col> getCols() {
        return cols;
    }
/*
    static public final Mapper<BeroepCode> mBeroepCode = new Mapper<BeroepCode>("beroep_code","bc")
            .withFields("version","vertaling_de","alfaKey_de","vertaling_fr","alfaKey_fr","vertaling_nl","alfaKey_nl","code","origineel")
            .withAutoGenFields("id")
            .withFromRecord(r -> {
                return new BeroepCode(
                        r.getInt("id"),
                        r.getInt("version"),
                        r.getString("vertaling_de"),
                        r.getString("alfaKey_de"),
                        r.getString("vertaling_fr"),
                        r.getString("alfaKey_fr"),
                        "",r.getString("vertaling_nl"),
                        r.getString("alfaKey_nl"),r.getString("code"),r.getBoolean("origineel"));
            })
            .withToStat((c,a)->{
                a.arg("id",c.getId(),"version",c.getVersion(),"vertaling_de",c.getVertaling_de());
                a.arg("alfaKey_de",c.getAlfaKey_de(),"vertaling_fr",c.getVertaling_fr(),"alfaKey_fr",c.getAlfaKey_fr(),"vertaling_nl",c.getVertalingNl(),"alfaKey_nl",c.getAlfaKey_nl(),"code",c.getCode(),"origineel",c.isOrigineel());
                return c;
            });

    static public void main(String...args){
        DbConfig dbConfig = new DbConfig(
                DataSourceBuilder.create().setMySql().setLogin("bevolking_user","bevolking_pwd").setUrl("jdbc:mysql://localhost:3306/bevolking")
        );


        System.out.println(mBeroepCode.createSelectById());
        System.out.println(mBeroepCode.createInsert());
        System.out.println(mBeroepCode.createUpdateForId());
        System.out.println(mBeroepCode.createDeleteForId());
    }*/
}
