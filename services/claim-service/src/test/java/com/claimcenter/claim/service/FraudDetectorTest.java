package com.claimcenter.claim.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FraudDetectorTest {
    private final FraudDetector fraudDetector = new FraudDetector();

    @Test
    void largeAmountRequiresReview() {
        assertEquals("FRAUD_REVIEW", fraudDetector.statusFor(new BigDecimal("50000")));
        assertTrue(fraudDetector.flagged("FRAUD_REVIEW"));
    }

    @Test
    void smallAmountIsSubmitted() {
        assertEquals("SUBMITTED", fraudDetector.statusFor(new BigDecimal("1200")));
    }
}
