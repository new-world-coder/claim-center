package com.claimcenter.common.tenant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class TenantHibernateFilterAspect {
    @PersistenceContext
    private EntityManager entityManager;

    @Before("execution(* com.claimcenter..service.*ApplicationService.*(..))")
    public void enableTenantFilter() {
        String tenantId = TenantContext.get();
        if (tenantId == null) {
            return;
        }
        Session session = entityManager.unwrap(Session.class);
        if (!session.getSessionFactory().getDefinedFilterNames().contains("tenantFilter")) {
            return;
        }
        if (session.getEnabledFilter("tenantFilter") == null) {
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
        }
    }
}
