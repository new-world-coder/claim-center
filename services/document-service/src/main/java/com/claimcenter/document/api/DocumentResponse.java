package com.claimcenter.document.api;

import java.util.UUID;

public record DocumentResponse(UUID id, String fileName, String contentType, UUID claimId) {
}
