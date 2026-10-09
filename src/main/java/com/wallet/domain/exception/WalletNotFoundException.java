package com.wallet.domain.exception;

import java.util.UUID;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(UUID walletId) {
        super(String.format("Carteira não encontrada para o identificador: %s", walletId));
    }
}
