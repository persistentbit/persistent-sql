package com.persistentbit.sql.statement;

import com.persistentbit.core.Immutable;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PStream;

import java.util.function.Function;

/**
 * Created by petermuys on 16/07/16.
 */
@Immutable
public class EJoinStats<T> {
    private final Function<PStream<Object>,T> resultConverter;
    private PList<JoinElement>  elements = PList.empty();
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

    private EJoinStats(Function<PStream<Object>,T> resultConverter,PList<JoinElement> elements){
        this.resultConverter = resultConverter;
        this.elements = elements;
    }



    class SelectBuilder {



    }
    public SelectBuilder select() {
        return new SelectBuilder();
    }

}
