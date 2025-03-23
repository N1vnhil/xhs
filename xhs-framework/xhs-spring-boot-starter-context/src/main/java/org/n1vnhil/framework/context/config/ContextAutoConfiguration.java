package org.n1vnhil.framework.context.config;

import org.n1vnhil.framework.context.filter.HeadUserId2ContextFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ContextAutoConfiguration {

    @Bean
    public HeadUserId2ContextFilter headUserId2ContextFilter() {
        return new HeadUserId2ContextFilter();
    }

}
