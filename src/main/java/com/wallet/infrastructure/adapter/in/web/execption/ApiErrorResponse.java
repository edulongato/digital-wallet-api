package com.wallet.infrastructure.adapter.in.web.execption;

import java.time.Instant;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
