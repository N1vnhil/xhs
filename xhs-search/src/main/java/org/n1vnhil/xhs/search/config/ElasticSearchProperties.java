package org.n1vnhil.xhs.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticSearchProperties {

    private String address;

}
