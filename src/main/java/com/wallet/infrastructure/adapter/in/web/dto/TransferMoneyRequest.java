package com.wallet.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferMoneyRequest(
        UUID sourceWalletId,
        UUID targetWalletId,
        BigDecimal amount

) {
}
