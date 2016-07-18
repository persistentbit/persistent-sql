package com.persistentbit.sql;

import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.statement.Db;
import com.persistentbit.sql.statement.EJoinStats;
import com.persistentbit.sql.statement.ETableStats;

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
        rowMapper.createDefault(Invoice.class)
                .addAllFieldsExcept("lines")
                .rename("number","invoice_nummer")
                .rename("fromPersonId","from_person_id")
                .rename("toPersonId","to_person_id");
        rowMapper.createDefault(InvoiceLine.class)
                .addAllFields()
                .rename("invoiceId","invoice_id");

    }

    public  ETableStats<Person> person = tableStats(Person.class,"PERSON");
    public  ETableStats<Invoice> invoice = tableStats(Invoice.class,"INVOICE");

    public ETableStats<InvoiceLine> invoiceLine = tableStats(InvoiceLine.class,"INVOICE_LINE");





    static public void main(String...args){
        DbInst db = new DbInst();
        PStream.sequence(0).limit(10).forEach(i -> {
            System.out.println(db.person.insert(new Person(0,"mup" + i,"pwd")));
        });
        db.person.select().getList().forEach(System.out::println);
        System.out.println(db.person.select().forId(7));
        System.out.println(db.person.select("where t.user_name = :username").arg("username","mup5").getOne());
        db.person.update(db.person.select().forId(8).get().withName("Peter Muys"));


        db.person.deleteForId(2);
        db.person.delete(db.person.select().forId(4).get());
        db.person.select().getList().forEach(System.out::println);
        System.out.println("Delete all: " + db.person.deleteAll());


        Person muys = db.person.insert(new Person(0,"petermuys","pwd"));
        Person axxes = db.person.insert(new Person(0,"axxes","pwd"));
        Invoice in = db.invoice.insert(new Invoice("2016-01",(int)muys.getId(),(int)axxes.getId()));
        Invoice in2 = db.invoice.insert(new Invoice("2016-02",(int)muys.getId(),(int)axxes.getId()));
        db.invoiceLine.insert(new InvoiceLine(0,in.getId(),"Werken januari"));
        db.invoiceLine.insert(new InvoiceLine(0,in.getId(),"Werken februari"));
        db.invoiceLine.insert(new InvoiceLine(0,in.getId(),"Werken maart"));
        db.invoiceLine.insert(new InvoiceLine(0,in.getId(),"Werken april"));

        EJoinStats join = db.invoice.asJoinable("inv")
                .leftOuterJoin(db.invoiceLine.asJoinable("line"),"line.invoice_id=inv.id")
                .leftOuterJoin(db.person.asJoinable("fPerson"),"fPerson.id=inv.from_person_id")
                .leftOuterJoin(db.person.asJoinable("toPerson"),"toPerson.id=inv.to_person_id")
         ;

        join.select("where line.id is not null").getList().forEach(System.out::println);

        System.out.println("START *********************");
        //PList<Tuple2<Invoice,InvoiceLine>> s = db.joinInvoiceLines.select().getList();
        //s.forEach(System.out::println);

        //db.joinInvoiceFrom(db.joinInvoiceLines.asJoinable()).select().getList().forEach(System.out::println);


        //s.groupByOrdered(t -> t._1).map(t -> t._1.withLines(t._2.map(ll -> ll._2).filter(r -> r != null))).forEach(System.out::println);


    }
}
