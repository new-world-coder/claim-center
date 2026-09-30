package com.claimcenter.tenant.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tenant_settings")
public class TenantSettingsEntity {
    @Id
    private UUID id;
    @Column(name = "tenant_id", nullable = false, unique = true)
    private String tenantId;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    private String timezone;
    @Column(name = "claim_auto_approve_limit", nullable = false)
    private BigDecimal claimAutoApproveLimit;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public BigDecimal getClaimAutoApproveLimit() { return claimAutoApproveLimit; }
    public void setClaimAutoApproveLimit(BigDecimal claimAutoApproveLimit) { this.claimAutoApproveLimit = claimAutoApproveLimit; }
}
