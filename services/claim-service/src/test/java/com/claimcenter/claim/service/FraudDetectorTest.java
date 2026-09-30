package com.claimcenter.claim.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FraudDetectorTest {
    private final FraudDetector fraudDetector = new FraudDetector();
    private final Instant afternoon = Instant.parse("2026-09-30T15:00:00Z");

    @Test
    void largeAmountRequiresReview() {
        FraudScore score = fraudDetector.score(new FraudCase(
                new BigDecimal("50000"),
                new BigDecimal("100000"),
                "Hail damage to the roof of the warehouse",
                0,
                afternoon));
        assertEquals("FRAUD_REVIEW", score.status());
        assertEquals("MEDIUM", score.band());
        assertTrue(score.flagged());
        assertTrue(score.score() >= 35);
        assertTrue(score.reasons().contains("Amount is at or above 50000"));
    }

    @Test
    void smallAmountIsSubmitted() {
        FraudScore score = fraudDetector.score(new FraudCase(
                new BigDecimal("1200"),
                new BigDecimal("25000"),
                "Water damage in kitchen",
                0,
                afternoon));
        assertEquals("SUBMITTED", score.status());
        assertEquals("LOW", score.band());
        assertEquals(0, score.score());
        assertFalse(score.flagged());
    }

    @Test
    void stackedSignalsReachHighBand() {
        FraudScore score = fraudDetector.score(new FraudCase(
                new BigDecimal("20000"),
                new BigDecimal("21000"),
                "stolen cash urgent",
                3,
                Instant.parse("2026-09-30T02:00:00Z")));
        assertEquals(100, score.score());
        assertEquals("HIGH", score.band());
        assertEquals("FRAUD_REVIEW", score.status());
        assertTrue(score.reasons().contains("Repeated claims on this policy"));
        assertTrue(score.reasons().contains("Submitted outside business hours"));
    }
}
