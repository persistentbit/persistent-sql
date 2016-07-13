package com.persistentbit.sql;

/**
 * @author Peter Muys
 * @since 4/07/2016
 */
public interface SqlArguments<B extends SqlArguments<B>>{

    B arg(String name, Object value);

    default B arg(String n1, Object o1, String n2, Object o2){
        return arg(n1,o1).arg(n2,o2);
    }
    default B arg(String n1, Object o1, String n2, Object o2, String n3, Object o3){
        return arg(n1,o1,n2,o2).arg(n3,o3);
    }
    default B arg(String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4){
        return arg(n1,o1,n2,o2,n3,o3).arg(n4,o4);
    }
    default B arg(String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4, String n5, Object o5){
        return arg(n1,o1,n2,o2,n3,o3,n4,o4).arg(n5,o5);
    }
    default B arg(String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4, String n5, Object o5, String n6, Object o6){
        return arg(n1,o1,n2,o2,n3,o3,n4,o4,n5,o5).arg(n6,o6);
    }
    default B arg(String n1, Object o1, String n2, Object o2, String n3, Object o3, String n4, Object o4, String n5, Object o5, String n6, Object o6, String n7, Object o7){
        return arg(n1,o1,n2,o2,n3,o3,n4,o4,n5,o5,n6,o6).arg(n7,o7);
    }
}
