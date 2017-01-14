package com.persistentbit.sql.old;

/**
 * @author Peter Muys
 * @since 13/07/16
 */
public abstract class AbstractTestWithTransactions{
    /*
    protected Logger log = Logger.getLogger(this.getClass().getName());
    protected InMemConnectionSupplier    dbConnector;
    protected TransactionRunnerPerThread trans;
    protected TestDbBuilderImpl          builder;
    protected SqlLoader                  loader;

    @Before
    public void setupTransactions() {
        dbConnector = new InMemConnectionSupplier();
        trans = new TransactionRunnerPerThread(dbConnector);
        builder = new TestDbBuilderImpl(new DbDerby(),null,trans);
        loader = new SqlLoader("/db/Tests.sql");
        if(builder.hasUpdatesThatAreDone()) {
            builder.dropAll();
        }
        builder.buildOrUpdate();
        assert builder.javaUpdaterCalled;
    }
    @After
    public void closeTransactions() {
        if(builder.hasUpdatesThatAreDone()) {
            builder.dropAll();
        }
        trans = null;
        dbConnector.close();
        dbConnector = null;
    }*/
}
