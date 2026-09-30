package com.claimcenter.audit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Filter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class AuditLogEntity {
    @Id
    private UUID id;
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    @Column(nullable = false)
    private String actor;
    @Column(name = "action", nullable = false)
    private String action;
    @Column(name = "resource_name", nullable = false)
    private String resourceName;
    @Column(name = "details")
    private String details;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
