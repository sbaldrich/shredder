package com.bc.shredder.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.util.List;

public class ShredderConfig {
    @JsonProperty("datasources")
    private List<DbConnection> dataSources;

    public List<DbConnection> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DbConnection> dataSources) {
        this.dataSources = dataSources;
    }

    public ShredderConfig(){}

    public static ShredderConfig init(String file) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        return mapper.readValue(ShredderConfig.class.getResourceAsStream(file), ShredderConfig.class);
    }
}
