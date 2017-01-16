package com.persistentbit.sql;

import com.persistentbit.core.testing.TestCase;
import com.persistentbit.core.testing.TestRunner;
import com.persistentbit.sql.staticsql.Insert;
import com.persistentbit.sql.staticsql.InsertWithGeneratedKeys;
import com.persistentbit.sql.staticsql.Query;
import com.persistentbit.sql.staticsql.expr.Sql;
import com.persistentbit.sql.test.*;

/**
 * TODOC
 *
 * @author petermuys
 * @since 14/01/17
 */
public class TestDbWork extends SQLTestTools{

	static final TestCase testDbBuilder = TestCase.name("DbBuilder").code(tr -> {
		tr.isFalse(run(builder.hasUpdatesThatAreDone()).orElseThrow());
		tr.isSuccess(run(builder.buildOrUpdate()));
		tr.isTrue(run(builder.hasUpdatesThatAreDone()).orElseThrow());
		tr.isSuccess(run(builder.dropAll()));

		tr.isFalse(run(builder.hasUpdatesThatAreDone()).orElseThrow());
		tr.isSuccess(run(builder.buildOrUpdate()));
		tr.isTrue(run(builder.hasUpdatesThatAreDone()).orElseThrow());
		tr.isSuccess(run(builder.dropAll()));
	});

	static final TestCase testInsertUpdate = TestCase.name("insert & update").code(tr -> {
		try {
			tr.isSuccess(run(builder.buildOrUpdate()));
			SPerson p1 = SPerson.build(b -> b
				.setId(0)
				.setUserName("pmu")
				.setPassword("test")
				.setAddress(new Address("Snoekstraat", 77, "9000", "Gent", "Belgium"))
			);
			SPerson p2 = SPerson.build(b -> b
				.setId(0)
				.setUserName("els")
				.setPassword("test2")
				.setAddress(new Address("Snoekstraat", 10, "9000", "Gent", "Belgium"))
			);

			tr.isEquals(run(Db.sPerson().insert(p1)).map(p -> p.getId()).orElseThrow(), 1);
			_SPerson tablePersons = Db.sPerson();
			InsertWithGeneratedKeys<Integer>
				i = Insert.into(tablePersons, tablePersons.val(p2)).withGeneratedKeys(tablePersons.id);
			tr.isEquals(run(i).orElseThrow(), 2);
			Query q1 = Query.from(tablePersons).distinct().orderByAsc(tablePersons.id);
			tr.isSuccess(run(q1.selection(tablePersons, Sql.val(1234), Sql.val(0).sub(tablePersons.id)))
							 .ifPresent(l -> l.getValue().forEach(item -> tr.info(item))));
			tr.isTrue(run(q1.where(tablePersons.address.houseNumber.between(5, 15)).selection(tablePersons))
						  .orElseThrow().size() == 1);
			tr.isNumbersEquals(run(Query.from(tablePersons).selection(Sql.val(1).count()).justOne()).orElseThrow(), 2);


			_SCompany tComp = Db.sCompany();
			SCompany muysSoftware = run(tComp.insert(SCompany.build(b -> b
				.setAdres(p1.getAddress()).setId(0)
			))).orElseThrow();
			SCompany schaubroek = run(tComp.insert(SCompany.build(b -> b
				.setAdres(new Address("N60", 159, "", "Nazareth", "Belgium")).setId(0)
			))).orElseThrow();
			_SInvoice     tInv     = Db.sInvoice();
			SInvoice      invoice1 =
				run(tInv.insert(new SInvoice(0, "2017-01", muysSoftware.getId(), schaubroek.getId()))).orElseThrow();
			_SInvoiceLine tInvLine = Db.sInvoiceLine();
			run(tInvLine.insert(new SInvoiceLine(1, invoice1.getId(), "Werk Januari")));

			run(Query.from(tInv).leftJoin(tInvLine).on(tInvLine.invoiceId.eq(tInv.id))
					.orderByAsc(tInv.invoiceNummer).orderByAsc(tInvLine.id).selection(tInv, tInvLine)).orElseThrow()
				.forEach(t -> tr.info(t));

		} finally {
			run(builder.dropAll());
		}


	});


	public void testAll() {
		TestRunner.runAndPrint(ModuleSql.createLogPrinter(true), TestDbWork.class);
	}

	public static void main(String[] args) {
		ModuleSql.createLogPrinter(true).registerAsGlobalHandler();
		new TestDbWork().testAll();
	}
}
