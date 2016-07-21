package com.persistentbit.sql.lazy;

import com.persistentbit.core.collections.PStream;
import com.persistentbit.core.collections.PStreamDelegate;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * User: petermuys
 * Date: 20/07/16
 * Time: 20:33
 */
public class LazyPStream<T> extends PStreamDelegate<T> {
    private Supplier<PStream<T>>    supplier;
    private PStream<T> delegate = null;

    public LazyPStream(Supplier<PStream<T>> supplier){
        this.supplier = supplier;
    }

    @Override
    protected synchronized PStream<T> getDelegate() {
        if(delegate == null){
            delegate = supplier.get();
        }
        return delegate;
    }


}
