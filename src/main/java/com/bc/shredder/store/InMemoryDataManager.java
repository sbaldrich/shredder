package com.bc.shredder.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

class InMemoryDataManager implements DataManager {
    private static final Logger log = LoggerFactory.getLogger(InMemoryDataManager.class);
    private final Map<String, DataSource> dataSourceCache;

    private final List<LogicalShard> shards = new ArrayList<>() {{
        var clusters = List.of("crateria", "norfair", "tourian");
        for (int c = 0; c < clusters.size(); c++) {
            String clusterName = clusters.get(c);
            for (int i = 1; i <= 10; i++) {
                add(new LogicalShard(clusterName, String.format("shredder%d", 10 * c + i)));
            }
        }
    }};

    public InMemoryDataManager(ShredderConfig config) {
        dataSourceCache = config.getDataSources()
                .stream()
                .map(DataSource::of)
                .collect(Collectors.toMap(DataSource::getName, x -> x));
    }

    @Override
    public Connection getConnection() throws SQLException {
        int chosenShard = ThreadLocalRandom.current().nextInt(shards.size());
        LogicalShard shard = shards.get(chosenShard);
        log.debug("Chose shard [id: {}, db: {}, schema: {}", chosenShard, shard.getDbConnection(), shard.getSchema());
        DataSource dataSource = dataSourceCache.get(shard.getDbConnection());
        Connection connection = dataSource.connection();
        connection.setSchema(shard.getSchema());
        return connection;
    }

    @Override
    public Connection getConnection(long shardKey) throws SQLException {
        int chosenShard = (int) ((shardKey >> 10) & ((1L << 13) - 1));
        log.debug("Determined shard: {}", chosenShard);
        LogicalShard shard = shards.get(chosenShard - 1);
        Connection connection = dataSourceCache.get(shard.getDbConnection()).connection();
        connection.setSchema(shard.getSchema());
        return connection;
    }

}
