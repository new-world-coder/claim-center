package com.claimcenter.customer.api;

import java.util.UUID;

public record CustomerResponse(UUID id, String fullName, String email, String phone) {
}
