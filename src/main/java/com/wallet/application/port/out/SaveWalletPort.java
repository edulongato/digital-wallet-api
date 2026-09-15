package com.wallet.application.port.out;

import com.wallet.domain.Wallet;

//Contrato para persistir a carteira alterada
public interface SaveWalletPort {
    void save(Wallet wallet);
}
