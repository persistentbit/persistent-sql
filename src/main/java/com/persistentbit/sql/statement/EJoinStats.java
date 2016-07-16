package com.persistentbit.sql.statement;

import com.persistentbit.core.collections.PList;

/**
 * Created by petermuys on 16/07/16.
 */
public class EJoinStats {
    private class JoinElement{
        public final String name;
        public final Joinable element;
        public final String joinSql;

        public JoinElement(String name, Joinable element, String joinSql) {
            this.name = name;
            this.element = element;
            this.joinSql = joinSql;
        }
    }
    private PList<JoinElement>  elements = PList.empty();

    EJoinStats  addJoin(String name,Joinable j1,String joinSql){
        elements = elements.plus(new JoinElement(name,j1,joinSql));
        return this;
    }

    class SelectBuilder {

    }
    public SelectBuilder select() {
        return new SelectBuilder();
    }
}
