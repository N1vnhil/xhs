package org.n1vnhil.xhs.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchHighLevelClient {

    @Autowired
    private ElasticSearchProperties elasticSearchProperties;

    private static final String COLON = ":";
    private static final String HTTP = "http";

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        String address = elasticSearchProperties.getAddress();
        String[] addressArr = address.split(COLON);
        String host = addressArr[0];
        int port = Integer.parseInt(addressArr[1]);
        HttpHost httpHost = new HttpHost(host, port, HTTP);
        return new RestHighLevelClient(RestClient.builder(httpHost));
    }

}
