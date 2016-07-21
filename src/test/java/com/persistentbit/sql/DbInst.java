package com.persistentbit.sql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.statement.Db;
import com.persistentbit.sql.statement.EJoinBuilder;
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
        /*rowMapper.createDefault(Person.class)
                .addAllFields()
                .rename("userName","USER_NAME")
        ;*/
        /*rowMapper.createDefault(Invoice.class)
                .addAllFieldsExcept("lines")
                .rename("number","invoice_nummer")
                .rename("fromPersonId","from_person_id")
                .rename("toPersonId","to_person_id");*/
        /*rowMapper.createDefault(InvoiceLine.class)
                .addAllFields()
                .rename("invoiceId","invoice_id");*/

    }

    public  ETableStats<Person> person = tableStats(Person.class);
    public  EJoinStats<Person> personAll = person.startJoin("p");
    public  ETableStats<Invoice> invoice = tableStats(Invoice.class);

    public ETableStats<InvoiceLine> invoiceLine = tableStats(InvoiceLine.class);





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

        System.out.println("With limit/offset");
        db.person.select().limitAndOffset(3,0).getList().forEach(System.out::println);

        System.out.println("Delete all: " + db.person.deleteAll());


        Person muys = db.person.insert(new Person(0,"petermuys","pwd"));
        Person axxes = db.person.insert(new Person(0,"axxes","pwd"));
        Invoice in = db.invoice.insert(new Invoice("2016-01",muys.getIdRef(),axxes.getIdRef()));
        Invoice in2 = db.invoice.insert(new Invoice("2016-02",muys.getIdRef(),axxes.getIdRef()));
        db.invoiceLine.insert(new InvoiceLine(0,in.getId(),"Werken januari"));
        db.invoiceLine.insert(new InvoiceLine(0,in.getId(),"Werken februari"));
        db.invoiceLine.insert(new InvoiceLine(0,in.getId(),"Werken maart"));
        db.invoiceLine.insert(new InvoiceLine(0,in.getId(),"Werken april"));


        EJoinStats<Invoice> invoiceLoader = db.invoice.startJoin("inv")
                .leftJoin(db.person,"toPerson").on("inv.to_person_id=toPerson.id").map((Invoice i,Person p)-> i.withToPerson(p.getValueRef()))
                //.leftJoin(db.person,"fromPerson").on("inv.from_person_id=fromPerson.id").map((Invoice i, Person p)-> i.withFromPerson(p))
                .extraMapping(i -> i.withLines(db.invoiceLine.select("where invoice_id=:invoiceId").arg("invoiceId",i.getId()).lazyLoading()))
                .extraMapping(i -> i.withFromPerson(db.personAll.select().lazyLoadingRef(i.getFromPersonId().getId())))
        ;


        System.out.println("All Persons: ");
        db.personAll.select().getList().forEach(System.out::println);

        PList<Invoice> invoices = invoiceLoader.select().getList();

        invoices.forEach(i -> {
            System.out.println(i);
            i.getLines().forEach(l -> {
                System.out.println("\t" + l);
            });
        });


        //EJoinStats<Invoice> j2 = j1.leftJoin(db.person.asJoinable("toPerson")).on("toPerson.id=inv.to_person_id");
        //EJoinStats<Tuple2<Invoice,InvoiceLine>> join = db.invoice.startJoin("inv")
        //        .leftJoin(db.person.asJoinable("fPerson")).on("fPerson.id=inv.from_person_id").<Invoice,Person>map((i,p)->i.withFromPerson(p))//.map(i -> i)//.map((i,p)-> i.withFromPersion(p))//.map((Invoice i, Person p) -> i.withFromPerson(p));
                //.leftJoin(db.person.asJoinable("toPerson")).on("toPerson.id=inv.to_person_id").map((i,p) -> i.withToPerson(p))
                //.leftJoin(db.invoiceLine.asJoinable("line")).on("line.invoice_id=inv.id").mapTuple();

        /*join.select("where line.id is not null or true").getList().groupByOrdered(l -> l.head(),l -> l.lastOpt().orElse(null)).map(t -> {
            Invoice i = (Invoice)t._1;
            return i.withLines((PList)(t._2).filter( p -> p != null));
        }).forEach(System.out::println);*/

        System.out.println("START *********************");
        //PList<Tuple2<Invoice,InvoiceLine>> s = db.joinInvoiceLines.select().getList();
        //s.forEach(System.out::println);

        //db.joinInvoiceFrom(db.joinInvoiceLines.asJoinable()).select().getList().forEach(System.out::println);


        //s.groupByOrdered(t -> t._1).map(t -> t._1.withLines(t._2.map(ll -> ll._2).filter(r -> r != null))).forEach(System.out::println);


    }
}
