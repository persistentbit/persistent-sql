package com.persistentbit.sql.staticsql;

/**
 * Created by petermuys on 2/10/16.
 */
public class ExprFindAllUsedTables{} //implements ExprVisitor<PSet<ETypeObject>> {
//
//    private ExprFindAllUsedTables(){
//
//    }
//
//    static public PSet<ETypeObject> findAll(Expr e){
//        return new ExprFindAllUsedTables().visit(e);
//    }
//
//    private PSet<ETypeObject> visit(Expr expr){
//        return (PSet<ETypeObject>)expr.accept(this);
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(EGroup group) {
//        return visit(group.getValue());
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(EMapper mapper) {
//        return visit(mapper.getExpr());
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprProperty v) {
//        return visit(v.getParent());
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprAndOr v) {
//        return visit(v.getLeft()).plusAll(visit(v.getRight()));
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprPropertyDate v) {
//        throw new ToDo();
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprPropertyDateTime v) {
//        throw new ToDo();
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprNumberToString v) {
//        return PSet.empty();
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprEnum v) {
//        return PSet.empty();
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprBoolean v) {
//        return PSet.empty();
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprDate v) {
//        return PSet.empty();
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprDateTime v) {
//        return PSet.empty();
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ETuple2 v) {
//        return visit(v.getV1()).plusAll(visit(v.getV2()));
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ETuple3 v) {
//        return visit(v.getV1()).plusAll(visit(v.getV2())).plusAll(visit(v.getV3()));
//    }
//    @Override
//    public PSet<ETypeObject> visit(ETuple4 v) {
//        return visit(v.getV1())
//                .plusAll(visit(v.getV2()))
//                .plusAll(visit(v.getV3()))
//                .plusAll(visit(v.getV4()))
//                ;
//    }    @Override
//    public PSet<ETypeObject> visit(ETuple5 v) {
//        return visit(v.getV1())
//                .plusAll(visit(v.getV2()))
//                .plusAll(visit(v.getV3()))
//                .plusAll(visit(v.getV4()))
//                .plusAll(visit(v.getV5()))
//                ;
//    }    @Override
//    public PSet<ETypeObject> visit(ETuple6 v) {
//        return visit(v.getV1())
//                .plusAll(visit(v.getV2()))
//                .plusAll(visit(v.getV3()))
//                .plusAll(visit(v.getV4()))
//                .plusAll(visit(v.getV5()))
//                .plusAll(visit(v.getV6()))
//                ;
//    }
//    public PSet<ETypeObject> visit(ETuple7 v) {
//        return visit(v.getV1())
//                .plusAll(visit(v.getV2()))
//                .plusAll(visit(v.getV3()))
//                .plusAll(visit(v.getV4()))
//                .plusAll(visit(v.getV5()))
//                .plusAll(visit(v.getV6()))
//                .plusAll(visit(v.getV7()))
//                ;
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprConstNumber v) {
//        return PSet.empty();
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprNumberCast v) {
//        return PSet.empty();
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ETypeObject v) {
//
//        if(v.getParent().isPresent() == false){
//            return PSet.val(v);
//        }
//        return PSet.empty();
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprCompare v) {
//        return visit(v.getLeft()).plusAll(visit(v.getRight()));
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprStringAdd v) {
//        return visit(v.getLeft()).plusAll(visit(v.getRight()));
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprStringLike v) {
//        return visit(v.getLeft()).plusAll(visit(v.getRight()));
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprConstString v) {
//        return PSet.empty();
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(ExprNumberBinOp v) {
//        return visit(v.getLeft()).plusAll(visit(v.getRight()));
//    }
//
//    @Override
//    public PSet<ETypeObject> visit(EValTable v) {
//        return PSet.empty();
//    }
//}
