package com.wallet.application.port.out;

import com.wallet.domain.Wallet;

import java.util.Optional;
import java.util.UUID;

// Contrato para buscar carteira
public interface LoadWalletPort {
    static Optional<Wallet> findById(UUID uuid) {
        return null;
    }
}
