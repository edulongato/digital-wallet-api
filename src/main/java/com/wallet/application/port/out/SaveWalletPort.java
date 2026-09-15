package com.wallet.application.port.out;

import com.wallet.domain.model.Wallet;

//Contrato para persistir a carteira alterada
public interface SaveWalletPort {
    void save(Wallet wallet);
}
