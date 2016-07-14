package com.persistentbit.sql.dbupdates;


import java.util.Arrays;
import java.util.List;

/**
 * User: petermuys
 * Date: 18/06/16
 * Time: 23:05
 */
public class DbUpdateRunner {
    private final List<DbUpdater> updaters ;

    public DbUpdateRunner(DbUpdater...updaters) {
        this.updaters = Arrays.asList(updaters);
    }

    public void update() {
        for(DbUpdater u : updaters){
            u.update();
        }
    }


}
