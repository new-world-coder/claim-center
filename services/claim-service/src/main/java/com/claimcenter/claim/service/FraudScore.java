package com.claimcenter.claim.service;

import java.util.List;

public record FraudScore(
        int score,
        String band,
        String status,
        boolean flagged,
        List<String> reasons
) {
}
