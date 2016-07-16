package com.persistentbit.sql;

import com.persistentbit.core.properties.FieldNames;

/**
 * User: petermuys
 * Date: 15/07/16
 * Time: 19:28
 */

public class Person {
    private final long id;
    private final String userName;
    private final String password;

    @FieldNames(names={"id","userName","password"})
    public Person(long id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public String toString() {
        return "TestRecord{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
