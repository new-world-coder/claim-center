package com.claimcenter.common.tenant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TenantDatabaseRegistry {
    private final String sharedUrl;
    private final String username;
    private final String password;
    private final Map<String, String> urls = new ConcurrentHashMap<>();
    private volatile long refreshedAt;

    public TenantDatabaseRegistry(String sharedUrl, String username, String password) {
        this.sharedUrl = sharedUrl;
        this.username = username;
        this.password = password;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public Optional<String> urlFor(String tenantId) {
        refresh(false);
        if (!urls.containsKey(tenantId)) {
            refresh(true);
        }
        return Optional.ofNullable(urls.get(tenantId));
    }

    private void refresh(boolean force) {
        long now = System.currentTimeMillis();
        if (!force && now - refreshedAt < 5000) {
            return;
        }
        synchronized (this) {
            if (!force && System.currentTimeMillis() - refreshedAt < 5000) {
                return;
            }
            try (Connection connection = DriverManager.getConnection(sharedUrl, username, password);
                 Statement statement = connection.createStatement();
                 ResultSet rows = statement.executeQuery(
                         "SELECT tenant_id, db_name FROM tenants WHERE tier = 'LARGE' AND db_name IS NOT NULL")) {
                urls.clear();
                while (rows.next()) {
                    urls.put(rows.getString("tenant_id"), swapDatabase(sharedUrl, rows.getString("db_name")));
                }
            } catch (SQLException ignored) {
                // Shared catalog may not exist yet during early startup.
            }
            refreshedAt = System.currentTimeMillis();
        }
    }

    public static String swapDatabase(String jdbcUrl, String databaseName) {
        int slash = jdbcUrl.lastIndexOf('/');
        if (slash < 0) {
            throw new IllegalArgumentException("Invalid JDBC URL");
        }
        int query = jdbcUrl.indexOf('?', slash);
        if (query < 0) {
            return jdbcUrl.substring(0, slash + 1) + databaseName;
        }
        return jdbcUrl.substring(0, slash + 1) + databaseName + jdbcUrl.substring(query);
    }
}
