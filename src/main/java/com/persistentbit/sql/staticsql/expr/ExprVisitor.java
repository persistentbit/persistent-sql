package com.persistentbit.sql.staticsql.expr;

/**
 * Created by petermuys on 2/10/16.
 */
public interface ExprVisitor<R> {
    /*default R visit(Expr e){
        return (R)e.accept(this);
    }*/
    R visit(EGroup group);
    R visit(EMapper mapper);
    R visit(ExprProperty v);
    R visit(ExprPropertyDate v);
    R visit(ExprPropertyDateTime v);
    R visit(ExprAndOr v);
    R visit(ExprNumberToString v);
    R visit(ExprBoolean v);
    R visit(ETuple2 v);
    R visit(ETuple3 v);
    R visit(ETuple4 v);
    R visit(ETuple5 v);
    R visit(ETuple6 v);
    R visit(ETuple7 v);
    R visit(ExprConstNumber v);
    R visit(ExprNumberCast v);
    R visit(ETypeObject v);
    R visit(ExprCompare v);
    R visit(ExprStringAdd v);
    R visit(ExprStringLike v);
    R visit(ExprConstString v);
    R visit(ExprNumberBinOp v);
    R visit(EValTable v);
    R visit(ExprDate v);
    R visit(ExprDateTime v);
    R visit(ExprEnum v);
    R visit(ExprIn v);
    R visit(ExprValueList v);
    R visit(ETypeSelection v);
}
