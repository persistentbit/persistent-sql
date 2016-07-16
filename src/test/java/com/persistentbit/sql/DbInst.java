package com.persistentbit.sql;

import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.statement.Db;
import com.persistentbit.sql.statement.ETableStats;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.function.Supplier;

/**
 * User: petermuys
 * Date: 16/07/16
 * Time: 16:55
 */
public class DbInst extends Db {




    public DbInst() {
        super(new InMemConnectionProvider());
        new TestDbUpdate.TestUpdater(runner).update();
        rowMapper.createDefault(Person.class)
                .addAllFields()
                .rename("userName","USER_NAME")


        ;
    }

    public ETableStats<Person> person(){
        return tableStats(Person.class,"PERSON");
    }


    static public void main(String...args){
        DbInst db = new DbInst();
        PStream.sequence(0).limit(10).forEach(i -> {
            System.out.println(db.person().insert(new Person(0,"mup" + i,"pwd")));
        });
        db.person().select().getList().forEach(System.out::println);
        System.out.println(db.person().select().forId(7));
        System.out.println(db.person().select("where t.user_name = :username").arg("username","mup5").getOne());
    }
}
