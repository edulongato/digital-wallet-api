package com.wallet.application.port.out;

import com.wallet.domain.model.Wallet;

import java.util.Optional;
import java.util.UUID;

// Contrato para buscar carteira
public interface LoadWalletPort {
    Optional<Wallet> findById(UUID id);


}
