package com.bc.shredder.store;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataManager {

    static DataManager get(ShredderConfig config){
        return new InMemoryDataManager(config);
    }

    Connection getConnection() throws SQLException;

    Connection getConnection(long shardKey) throws SQLException;
}
