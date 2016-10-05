package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.core.utils.NotYet;
import com.persistentbit.sql.staticsql.expr.*;


/**
 * Created by petermuys on 2/10/16.
 */
public class ExprExpand implements ExprVisitor<PList<Expr>>{

    static public PList<Expr> exapand(Expr e){
        return new ExprExpand().visit(e);
    }

    private PList<Expr> visit(Expr expr){
        return (PList<Expr>)expr.accept(this);
    }

    @Override
    public PList<Expr> visit(EMapper mapper) {
        return visit(mapper.getExpr());
    }

    private Expr getProperty(Expr parent, String propertyName){
        if(parent instanceof ETypeObject){
            // We have a table column
            ETypeObject obj = (ETypeObject)parent;
            PList<Tuple2<String,Expr>> props = obj._all();
            return props.find(tp ->tp._1.equals(propertyName)).get()._2;
        } else if(parent instanceof ExprProperty){
            //We have a embedded object
            ExprProperty ep = (ExprProperty)parent;
            return getProperty(ep.getParent(),ep.getPropertyName());
        } else {
            throw new RuntimeException("Don't know what to do with:" + parent + " and propertyName " + propertyName);
        }
    }

    @Override
    public PList<Expr> visit(ExprEnum v) {
        return PList.val(v);
    }

    @Override
    public PList<Expr> visit(ExprPropertyDate v) {
        throw new NotYet();
    }

    @Override
    public PList<Expr> visit(ExprPropertyDateTime v) {
        throw new NotYet();
    }

    @Override
    public PList<Expr> visit(ExprProperty v) {
        ETypeObject parent = (ETypeObject)v.getParent();
        Expr subProp = getProperty(v.getParent(),v.getPropertyName());
        return PList.val(subProp);
    }

    @Override
    public PList<Expr> visit(ExprAndOr v) {
        return PList.val(v);
    }



    @Override
    public PList<Expr> visit(ExprNumberToString v) {
        return PList.val(v);
    }

    @Override
    public PList<Expr> visit(ExprBoolean v) {
        return PList.val(v);
    }

    @Override
    public PList<Expr> visit(ExprDate v) {
        return PList.val(v);
    }

    @Override
    public PList<Expr> visit(ExprDateTime v) {
        return PList.val(v);
    }

    @Override
    public PList<Expr> visit(ETuple2 v) {
        return visit(v.getV1()).plusAll(visit(v.getV2()));
    }

    @Override
    public PList<Expr> visit(ETuple3 v) {
        return visit(v.getV1()).plusAll(visit(v.getV2())).plusAll(visit(v.getV3()));
    }

    @Override
    public PList<Expr> visit(ETuple4 v) {
        return visit(v.getV1())
                .plusAll(visit(v.getV2()))
                .plusAll(visit(v.getV3()))
                .plusAll(visit(v.getV4()))
                ;
    }    @Override
    public PList<Expr> visit(ETuple5 v) {
        return visit(v.getV1())
                .plusAll(visit(v.getV2()))
                .plusAll(visit(v.getV3()))
                .plusAll(visit(v.getV4()))
                .plusAll(visit(v.getV5()))
                ;
    }    @Override
    public PList<Expr> visit(ETuple6 v) {
        return visit(v.getV1())
                .plusAll(visit(v.getV2()))
                .plusAll(visit(v.getV3()))
                .plusAll(visit(v.getV4()))
                .plusAll(visit(v.getV5()))
                .plusAll(visit(v.getV6()))
                ;
    }
    @Override
    public PList<Expr> visit(ETuple7 v) {
        return visit(v.getV1())
                .plusAll(visit(v.getV2()))
                .plusAll(visit(v.getV3()))
                .plusAll(visit(v.getV4()))
                .plusAll(visit(v.getV5()))
                .plusAll(visit(v.getV6()))
                .plusAll(visit(v.getV7()))
                ;
    }

    @Override
    public PList<Expr> visit(ExprConstNumber v) {
        return PList.val(v);
    }

    @Override
    public PList<Expr> visit(ExprNumberCast v) {
        return PList.val(v);
    }

    @Override
    public PList<Expr> visit(ETypeObject v) {
        PList<Expr> result = PList.empty();
        PList<Tuple2<String,Expr>> props = v._all();
        return props.map(t -> visit(t._2)).<Expr>flatten().plist();
    }

    @Override
    public PList<Expr> visit(ExprCompare v) {
        return PList.val(v);
    }

    @Override
    public PList<Expr> visit(ExprStringAdd v) {
        return PList.val(v);
    }

    @Override
    public PList<Expr> visit(ExprConstString v) {
        return PList.val(v);
    }

    @Override
    public PList<Expr> visit(ExprNumberBinOp v) {
        return PList.val(v);
    }

    @Override
    public PList<Expr> visit(EValTable v) {
        return TableValueToExpressions.toExpr(v);
    }

}
