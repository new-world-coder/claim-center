package com.claimcenter.tenant.service;

import com.claimcenter.common.tenant.TenantDatabaseRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
@Component
public class DedicatedDatabaseProvisioner {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public DedicatedDatabaseProvisioner(
            @Value("${spring.datasource.url}") String jdbcUrl,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public void provision(String databaseName) {
        if (!databaseName.matches("[a-z][a-z0-9_]{0,40}")) {
            throw new IllegalArgumentException("Invalid database name");
        }
        try (Connection admin = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = admin.createStatement()) {
            admin.setAutoCommit(true);
            boolean exists;
            try (var rows = statement.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + databaseName + "'")) {
                exists = rows.next();
            }
            if (!exists) {
                try (Statement create = admin.createStatement()) {
                    create.executeUpdate("CREATE DATABASE " + databaseName);
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not provision dedicated database " + databaseName, exception);
        }
        String dedicatedUrl = TenantDatabaseRegistry.swapDatabase(jdbcUrl, databaseName);
        try (Connection dedicated = DriverManager.getConnection(dedicatedUrl, username, password);
             Statement statement = dedicated.createStatement()) {
            String sql = new String(new ClassPathResource("db/schema.sql").getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            for (String command : sql.split(";")) {
                String trimmed = command.trim();
                if (!trimmed.isEmpty()) {
                    statement.execute(trimmed);
                }
            }
        } catch (SQLException | IOException exception) {
            throw new IllegalStateException("Could not initialize dedicated database " + databaseName, exception);
        }
    }

    public void seedSample(String databaseName, String tenantId) {
        String dedicatedUrl = TenantDatabaseRegistry.swapDatabase(jdbcUrl, databaseName);
        String customerId = "33333333-3333-3333-3333-333333333333";
        String policyId = "44444444-4444-4444-4444-444444444444";
        try (Connection dedicated = DriverManager.getConnection(dedicatedUrl, username, password);
             Statement statement = dedicated.createStatement()) {
            statement.execute("INSERT INTO customers (id, tenant_id, full_name, email, phone, created_at) "
                    + "SELECT '" + customerId + "', '" + tenantId + "', 'Big Co Insured', 'insured@bigco.test', '555-0100', CURRENT_TIMESTAMP "
                    + "WHERE NOT EXISTS (SELECT 1 FROM customers WHERE id = '" + customerId + "')");
            statement.execute("INSERT INTO policies (id, tenant_id, policy_number, customer_id, status, coverage_amount, created_at) "
                    + "SELECT '" + policyId + "', '" + tenantId + "', 'POL-9001', '" + customerId + "', 'ACTIVE', 100000.00, CURRENT_TIMESTAMP "
                    + "WHERE NOT EXISTS (SELECT 1 FROM policies WHERE id = '" + policyId + "')");
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not seed dedicated database " + databaseName, exception);
        }
    }
}
