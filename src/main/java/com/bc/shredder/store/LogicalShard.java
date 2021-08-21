package com.bc.shredder.store;

class LogicalShard {
    private final String dbConnection;
    private final String schema;

    public LogicalShard(String dbConnection, String schema) {
        this.dbConnection = dbConnection;
        this.schema = schema;
    }

    public String getDbConnection() {
        return dbConnection;
    }

    public String getSchema() {
        return schema;
    }
}
