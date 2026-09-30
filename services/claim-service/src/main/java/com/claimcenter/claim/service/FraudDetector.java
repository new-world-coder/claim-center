package com.claimcenter.claim.service;

import java.math.BigDecimal;

public class FraudDetector {
    private static final BigDecimal REVIEW_THRESHOLD = new BigDecimal("50000");

    public String statusFor(BigDecimal amount) {
        if (amount.compareTo(REVIEW_THRESHOLD) >= 0) {
            return "FRAUD_REVIEW";
        }
        return "SUBMITTED";
    }

    public boolean flagged(String status) {
        return "FRAUD_REVIEW".equals(status);
    }
}
