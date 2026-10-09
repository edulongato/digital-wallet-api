package com.wallet.application.port.in;


import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

// Comando com validações de entrada
public record TransferMoneyCommand(
        UUID sourceWalletId,
        UUID targetWalletId,
        BigDecimal amount
) {

    public TransferMoneyCommand {
        Objects.requireNonNull(sourceWalletId, "A carteira de origem é obrigatória.");
        Objects.requireNonNull(targetWalletId, "A carteira de destino é obrigatória.");
        Objects.requireNonNull(amount, "O valor é obrigatório.");

        if(sourceWalletId.equals(targetWalletId)) {
            throw new IllegalArgumentException("A carteira de origem e destino não podem ser iguais.");

        }
        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser maior que zero.");
        }
    }
}
