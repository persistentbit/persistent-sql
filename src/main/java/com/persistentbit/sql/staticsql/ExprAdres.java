package com.persistentbit.sql.staticsql;



import com.persistentbit.core.tuples.Tuple2;

import java.util.function.Function;

class Adres{

}

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class ExprAdres implements ETypeObject<Adres>{/*{
    private Expr parent;

    public ExprAdres(Expr parent) {
        this.parent = parent;
    }
    public ETypeNumber id=new ExprPropertyNumber(this,"id");
    public ExprUser    updateUser=new ExprUser(new ExprProperty(this,"updateUser"));
    public ETypeNumber  created_user_id=new ExprPropertyNumber(this,"created_user_id");
    public ETypeNumber  updated_user_id=new ExprPropertyNumber(this,"updated_user_id");
    public ExprInfoType  it=new ExprInfoType(new ExprProperty(this,"it"));
    public ETypeNumber  huisNummer=new ExprPropertyNumber(this,"huisNummer");
    public ETypeString  straatNaam=new ExprPropertyString(this,"straatNaam");

    */

    private Expr __parent;

    public ExprAdres(Expr __parent) {
        this.__parent = __parent;
    }

    @Override
    public String toString() {
        return __parent.toString();
    }
    public ETypeNumber<Integer> id = new ExprPropertyNumber<>(this,"id");
    public ETypeNumber<Integer> huisNummer = new ExprPropertyNumber<>(this,"huisNummer");
    public ETypeString  straat = new ExprPropertyString(this,"straat");


    static public <V1,V2,E1 extends ETypeObject<V1>,E2 extends ETypeObject<V2>> Expr<Tuple2<V1,V2>> view(E1 v1, E2 v2, Function<Tuple2<E1,E2>,ETypeBoolean> joinOn){
        return null;
    }

    static public void main(String...args){
        ExprAdres a = new ExprAdres(new ExprVar("a"));
        ExprAdres a2 = new ExprAdres(new ExprVar("a2"));
        System.out.println(a.id.gtEq(a.id.add(Expr.val(100).mul(10)).div(a.id)));
        System.out.println(a.straat.add(" ").add(a.huisNummer.asString()));
        Expr<Tuple2<Adres,Adres>> join = view(a,a2,t -> t._1.id.eq(t._2.id));

    }
}
