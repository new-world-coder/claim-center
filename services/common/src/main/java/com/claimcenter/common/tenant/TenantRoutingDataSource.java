package com.claimcenter.common.tenant;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {
    private final DataSource shared;
    private final TenantDatabaseRegistry registry;
    private final Map<String, HikariDataSource> dedicated = new ConcurrentHashMap<>();

    public TenantRoutingDataSource(DataSource shared, TenantDatabaseRegistry registry) {
        this.shared = shared;
        this.registry = registry;
        setDefaultTargetDataSource(shared);
        setTargetDataSources(Map.of("shared", shared));
        setLenientFallback(true);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        if (TenantContext.get() == null || !"LARGE".equals(TenantContext.tier())) {
            return "shared";
        }
        return TenantContext.get();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        Object key = determineCurrentLookupKey();
        if (key == null || "shared".equals(key)) {
            return shared;
        }
        return dedicated.computeIfAbsent(key.toString(), this::openDedicated);
    }

    private HikariDataSource openDedicated(String tenantId) {
        String url = registry.urlFor(tenantId)
                .orElseThrow(() -> new IllegalStateException("Dedicated database is not provisioned for " + tenantId));
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(registry.username());
        config.setPassword(registry.password());
        config.setMaximumPoolSize(4);
        return new HikariDataSource(config);
    }
}
