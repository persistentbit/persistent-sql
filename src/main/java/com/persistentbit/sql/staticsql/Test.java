package com.persistentbit.sql.staticsql;



/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public class Test {}/*{
    interface Select<T>{

        default <T2,R> Select<R> withMany(Select<T2> t2, Function<Tuple2<T, PList<T2>>, R> mapper){
            return new WithMany2<>(this,t2,mapper);
        }

        PList<String>   selectList();
    }
    static class SelectExpr<T> implements  Select<T>{
        public final Expr<T> expr;

        public SelectExpr(Expr<T> expr) {
            this.expr = expr;
        }

        @Override
        public PList<String> selectList() {
            return PList.val(expr.toString());
        }
    }

    static <T> Select<T>    select(Expr<T> expr){
        return new SelectExpr<>(expr);
    }

    static class Group2<T1,T2,R> implements Select<R>{
        private final Select<T1>  t1;
        private final Select<T2>  t2;
        private final Function<Tuple2<T1,T2>,R> mapper;

        public Group2(Select<T1> t1, Select<T2> t2, Function<Tuple2<T1, T2>, R> mapper) {
            this.t1 = t1;
            this.t2 = t2;
            this.mapper = mapper;
        }

        @Override
        public PList<String> selectList() {
            return t1.selectList().plusAll(t2.selectList());
        }
    }
    static class Group3<T1,T2,T3,R> implements Select<R>{
        private final Select<T1>  t1;
        private final Select<T2>  t2;
        private final Select<T3>  t3;
        private final Function<Tuple3<T1,T2,T3>,R> mapper;

        public Group3(Select<T1> t1, Select<T2> t2, Select<T3> t3, Function<Tuple3<T1, T2, T3>, R> mapper) {
            this.t1 = t1;
            this.t2 = t2;
            this.t3 = t3;
            this.mapper = mapper;
        }

        @Override
        public PList<String> selectList() {
            return t1.selectList().plusAll(t2.selectList()).plusAll(t3.selectList());
        }
    }

    static class WithMany2<T1,T2,R> implements Select<R>{
        private final Select<T1>  t1;
        private final Select<T2>  t2;
        private final Function<Tuple2<T1,PList<T2>>,R> mapper;

        public WithMany2(Select<T1> t1, Select<T2> t2, Function<Tuple2<T1, PList<T2>>, R> mapper) {
            this.t1 = t1;
            this.t2 = t2;
            this.mapper = mapper;
        }

        @Override
        public PList<String> selectList() {
            return t1.selectList().plusAll(t2.selectList());
        }
    }

    static <T1,T2,R> Select<R>  group(Select<T1> e1,Select<T2> e2,Function<Tuple2<T1,T2>,R> mapper){
        return new Group2<>(e1,e2,mapper);
    }
    static <T1,T2,T3,R> Select<R>  group(Select<T1> e1,Select<T2> e2,Select<T3> e3,Function<Tuple3<T1,T2,T3>,R> mapper){
        return new Group3<>(e1,e2,e3,mapper);
    }


    public static class Full{
        public Adres a;
        public User cu;
        public User uu;
        public PList<User> allUsers;

        public Full(Adres a, User cu, User uu,PList<User> allUsers) {
            this.a = a;
            this.cu = cu;
            this.uu = uu;
            this.allUsers = null;
        }
    }

    static class Db{
        public ExprAdres adres(String name){
            return new ExprAdres(new ExprVar(name));
        }
        public ExprUser user(String name){
            return new ExprUser(new ExprVar(name));
        }
        public ExprInfoType infoType(String name){
            return new ExprInfoType(new ExprVar(name));
        }
    }

    static public void main(String...args){

        Db db = new Db();

        ExprAdres a = db.adres("adres");
        ExprUser createUser = db.user("cu");
        ExprUser updateUser = db.user("uu");
        ExprUser adresUser = db.user("adresUser");


        ETypeObject join = a
                .join(createUser,createUser.id.eq(a.created_user_id))
                .join(updateUser,updateUser.id.eq(a.updated_user_id))
                .join(adresUser,adresUser.adres_id.eq(a.id))
        ;

        Select<Full> sel =
                group(select(a),select(createUser),select(updateUser), t -> new Full(t.get_1(),t.get_2(),t.get_3(),PList.empty()))
                .withMany(select(adresUser),t -> {
                    t.get_1().allUsers = t.get_2();
                    return t.get_1();
        });

        String sql = "select " + sel.selectList().toString(",") + " from " + join.toString();
        System.out.println(sql);



    }

}
*/