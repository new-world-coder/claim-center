package com.claimcenter.common.tenant;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(name = "claimcenter.tenancy.routing", havingValue = "true")
public class RoutingDataSourceConfig {
    @Bean
    TenantDatabaseRegistry tenantDatabaseRegistry(DataSourceProperties properties) {
        return new TenantDatabaseRegistry(properties.getUrl(), properties.getUsername(), properties.getPassword());
    }

    @Bean
    @Primary
    DataSource dataSource(DataSourceProperties properties, TenantDatabaseRegistry registry) {
        HikariDataSource shared = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        TenantRoutingDataSource routing = new TenantRoutingDataSource(shared, registry);
        routing.afterPropertiesSet();
        return routing;
    }
}
