package com.persistentbit.sql.staticsql;

/**
 * Created by petermuys on 2/10/16.
 */
public class ExprToSql{}

//implements ExprVisitor<String>{
//
//    private final Function<ETypeObject,Optional<String>> instanceName;
//    private final DbType dbType;
//
//    private ExprToSql(Function<ETypeObject,Optional<String>> instanceName, DbType dbType) {
//        this.instanceName = instanceName;
//        this.dbType = dbType;
//    }
//
//    static public String toSql(Expr e,Function<ETypeObject,Optional<String>> instanceName, DbType dbType){
//        return new ExprToSql(instanceName,dbType).visit(e);
//    }
//
//    private String visit(Expr expr){
//        return (String)expr.accept(this);
//    }
//
//    @Override
//    public String visit(EGroup group) {
//        return "(" + visit(group.getValue()) + ")";
//    }
//
//    @Override
//    public String visit(ExprPropertyDate v) {
//        throw new ToDo();
//    }
//
//    @Override
//    public String visit(ExprPropertyDateTime v) {
//        throw new ToDo();
//    }
//
//    @Override
//    public String visit(EMapper mapper) {
//        return visit(mapper.getExpr());
//    }
//
//    @Override
//    public String visit(ExprProperty v) {
//        ETypeObject parent = (ETypeObject)v.getParent();
//
//        if(parent.getParent().isPresent() == false){
//            return visit(parent)+"." + v.getColumnName();
//        } else {
//            String parentStr = visit(parent);
//            if(parentStr.endsWith(".")){
//                return parentStr + v.getColumnName();
//            }
//            return visit(parent)+"_" + v.getColumnName();
//        }
//
//    }
//
//    @Override
//    public String visit(ExprAndOr v) {
//        String res = visit(v.getLeft());
//        switch(v.getLogicType()){
//            case and: res += " AND "; break;
//            case or: res += " OR "; break;
//            default: throw new IllegalArgumentException(v.getLogicType().toString());
//        }
//        return res + visit(v.getRight());
//    }
//
//    @Override
//    public String visit(ExprNumberToString v) {
//        return dbType.numberToString(visit(v.getNumber()),20);
//    }
//
//    @Override
//    public String visit(ExprBoolean v) {
//        return v.getValue() == null ?  "null" : (v.getValue() ? "TRUE" : "FALSE");
//    }
//
//    @Override
//    public String visit(ExprDate v) {
//        if(v == null){
//            return "null";
//        }
//        return dbType.asLiteralDate(v.getValue());
//    }
//
//    @Override
//    public String visit(ExprEnum v) {
//        Enum value = v.getValue();
//        if(value ==null){
//            return "null";
//        }
//        return dbType.asLiteralString(value.name());
//    }
//
//    @Override
//    public String visit(ExprDateTime v) {
//        return dbType.asLiteralDateTime(v.getValue());
//    }
//
//    @Override
//    public String visit(ETuple2 v) {
//        return "(" + visit(v.getV1()) + "," + visit(v.getV2())+")";
//    }
//
//    @Override
//    public String visit(ETuple3 v) {
//        return "(" +
//                visit(v.getV1()) + "," +
//                visit(v.getV2()) + "," +
//                visit(v.getV3())
//                +")";
//    }
//    @Override
//    public String visit(ETuple4 v) {
//        return "(" +
//                visit(v.getV1()) + "," +
//                visit(v.getV2()) + "," +
//                visit(v.getV3()) + "," +
//                visit(v.getV4())
//                +")";
//    }
//    @Override
//    public String visit(ETuple5 v) {
//        return "(" +
//                visit(v.getV1()) + "," +
//                visit(v.getV2()) + "," +
//                visit(v.getV3()) + "," +
//                visit(v.getV4()) + "," +
//                visit(v.getV5())
//                +")";
//    }
//    @Override
//    public String visit(ETuple6 v) {
//        return "(" +
//                visit(v.getV1()) + "," +
//                visit(v.getV2()) + "," +
//                visit(v.getV3()) + "," +
//                visit(v.getV4()) + "," +
//                visit(v.getV5()) + "," +
//                visit(v.getV6())
//                +")";
//    }
//
//    @Override
//    public String visit(ETuple7 v) {
//        return "(" +
//                visit(v.getV1()) + "," +
//                visit(v.getV2()) + "," +
//                visit(v.getV3()) + "," +
//                visit(v.getV4()) + "," +
//                visit(v.getV5()) + "," +
//                visit(v.getV6()) + "," +
//                visit(v.getV7())
//                +")";
//    }
//    @Override
//    public String visit(ExprConstNumber v) {
//        return v.getValue() == null ? "null" : v.getValue().toString();
//    }
//
//    @Override
//    public String visit(ExprNumberCast v) {
//        throw new ToDo();
//    }
//
//    @Override
//    public String visit(ETypeObject v) {
//        if(v.getParent().isPresent() == false){
//            return instanceName.apply(v).orElse(v.getFullTableName());
//        } else {
//            return visit((Expr)v.getParent().get());
//        }
//
//    }
//
//    @Override
//    public String visit(ExprCompare v) {
//        return visit(v.getLeft()) + v.getCompType() + visit(v.getRight());
//    }
//
//    @Override
//    public String visit(ExprStringAdd v) {
//        return dbType.concatStrings(visit(v.getLeft()),visit(v.getRight()));
//    }
//
//    @Override
//    public String visit(ExprStringLike v) {
//        return visit(v.getLeft()) + " LIKE " + visit(v.getRight());
//    }
//
//    @Override
//    public String visit(ExprConstString v) {
//        return dbType.asLiteralString(v.getValue());
//    }
//
//    @Override
//    public String visit(ExprNumberBinOp v) {
//        return visit(v.getLeft())+v.getBinOp() + visit(v.getRight());
//    }
//
//    @Override
//    public String visit(EValTable v) {
//        ETypeObject obj = v.getTable();
//        PList<Expr> constExpressions = TableValueToExpressions.toExpr(v);
//        return "VALUES (" + constExpressions.map(e -> visit(e)).toString(", ") + ")";
//    }
//}
