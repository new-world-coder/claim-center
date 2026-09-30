package com.claimcenter.claim.service;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FraudDetector {
    private static final BigDecimal HIGH_AMOUNT = new BigDecimal("50000");
    private static final BigDecimal ELEVATED_AMOUNT = new BigDecimal("20000");
    private static final BigDecimal NOTABLE_AMOUNT = new BigDecimal("10000");
    private static final BigDecimal ROUND_UNIT = new BigDecimal("1000");
    private static final BigDecimal NEAR_FULL = new BigDecimal("0.95");
    private static final BigDecimal MOST = new BigDecimal("0.80");
    private static final BigDecimal HALF = new BigDecimal("0.50");
    private static final List<String> KEYWORDS = List.of(
            "total loss", "stolen", "cash", "wire", "urgent", "unoccupied", "no police");

    public FraudScore score(FraudCase claim) {
        List<String> reasons = new ArrayList<>();
        int total = 0;
        total += amountPoints(claim.amount(), reasons);
        total += utilizationPoints(claim.amount(), claim.coverageAmount(), reasons);
        total += roundAmountPoints(claim.amount(), reasons);
        total += narrativePoints(claim.description(), reasons);
        total += velocityPoints(claim.recentClaimsOnPolicy(), reasons);
        total += timingPoints(claim.submittedAt(), reasons);
        int score = Math.min(total, 100);
        String band = bandFor(score);
        boolean flagged = !"LOW".equals(band);
        String status = flagged ? "FRAUD_REVIEW" : "SUBMITTED";
        return new FraudScore(score, band, status, flagged, List.copyOf(reasons));
    }

    private int amountPoints(BigDecimal amount, List<String> reasons) {
        if (amount.compareTo(HIGH_AMOUNT) >= 0) {
            reasons.add("Amount is at or above 50000");
            return 40;
        }
        if (amount.compareTo(ELEVATED_AMOUNT) >= 0) {
            reasons.add("High claim amount");
            return 25;
        }
        if (amount.compareTo(NOTABLE_AMOUNT) >= 0) {
            reasons.add("Elevated claim amount");
            return 12;
        }
        return 0;
    }

    private int utilizationPoints(BigDecimal amount, BigDecimal coverage, List<String> reasons) {
        if (coverage == null || coverage.signum() <= 0) {
            return 0;
        }
        BigDecimal ratio = amount.divide(coverage, 4, java.math.RoundingMode.HALF_UP);
        if (ratio.compareTo(NEAR_FULL) >= 0) {
            reasons.add("Claim uses nearly all of the coverage");
            return 25;
        }
        if (ratio.compareTo(MOST) >= 0) {
            reasons.add("Claim uses most of the coverage");
            return 15;
        }
        if (ratio.compareTo(HALF) >= 0) {
            reasons.add("Claim is half of the coverage or more");
            return 5;
        }
        return 0;
    }

    private int roundAmountPoints(BigDecimal amount, List<String> reasons) {
        if (amount.compareTo(new BigDecimal("5000")) < 0) {
            return 0;
        }
        if (amount.remainder(ROUND_UNIT).compareTo(BigDecimal.ZERO) != 0) {
            return 0;
        }
        reasons.add("Round claim amount");
        return 10;
    }

    private int narrativePoints(String description, List<String> reasons) {
        String text = description == null ? "" : description.trim();
        int points = 0;
        if (text.length() < 12) {
            reasons.add("Description is too short");
            points += 15;
        }
        String lower = text.toLowerCase(Locale.ROOT);
        int keywordPoints = 0;
        for (String keyword : KEYWORDS) {
            if (lower.contains(keyword) && keywordPoints < 24) {
                reasons.add("Description mentions " + keyword);
                keywordPoints += 8;
            }
        }
        return points + Math.min(keywordPoints, 24);
    }

    private int velocityPoints(int recentClaims, List<String> reasons) {
        if (recentClaims >= 3) {
            reasons.add("Repeated claims on this policy");
            return 25;
        }
        if (recentClaims >= 1) {
            reasons.add("Prior claim on this policy");
            return 10;
        }
        return 0;
    }

    private int timingPoints(java.time.Instant submittedAt, List<String> reasons) {
        if (submittedAt == null) {
            return 0;
        }
        int hour = submittedAt.atZone(ZoneOffset.UTC).getHour();
        if (hour < 6) {
            reasons.add("Submitted outside business hours");
            return 8;
        }
        return 0;
    }

    private String bandFor(int score) {
        if (score >= 60) {
            return "HIGH";
        }
        if (score >= 35) {
            return "MEDIUM";
        }
        return "LOW";
    }
}
