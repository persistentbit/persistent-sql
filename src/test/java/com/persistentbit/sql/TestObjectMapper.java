package com.persistentbit.sql;

import com.persistentbit.sql.objectmappers.InMemoryRow;
import com.persistentbit.sql.objectmappers.ObjectRowMapper;
import org.junit.Test;

/**
 * @author Peter Muys
 * @since 18/07/2016
 */
public class TestObjectMapper {
    @Test
    public void testObjectWriter() {
        Person p = new Person(1,"user","pwd");
        ObjectRowMapper mapper = new ObjectRowMapper();
        mapper.createDefault(Person.class).addAllFields().rename("userName","user_name");
        mapper.createDefault(Invoice.class).addAllFieldsExcept("lines");
        InMemoryRow row = new InMemoryRow();
        mapper.write(p,row);
        System.out.println(row);
        System.out.println(mapper.read(Person.class,row));

        Invoice in = new Invoice("1234",p.getIdRef(),p.getIdRef());
        row = new InMemoryRow();
        mapper.write(in,row);
        System.out.println(row);
        System.out.println(mapper.read(Invoice.class,row));
    }
}
