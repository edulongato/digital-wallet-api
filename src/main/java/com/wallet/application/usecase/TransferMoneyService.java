package com.wallet.application.usecase;

import com.wallet.application.port.in.TransferMoneyCommand;
import com.wallet.application.port.in.TransferMoneyUseCase;
import com.wallet.application.port.out.LoadWalletPort;
import com.wallet.application.port.out.SaveWalletPort;
import com.wallet.domain.model.Wallet;
import com.wallet.domain.exception.WalletNotFoundException;

public class TransferMoneyService implements TransferMoneyUseCase {

    private final LoadWalletPort loadWalletPort;
    private final SaveWalletPort saveWalletPort;

    public TransferMoneyService(LoadWalletPort loadWalletPort, SaveWalletPort saveWalletPort) {
        this.loadWalletPort = loadWalletPort;
        this.saveWalletPort = saveWalletPort;
    }

    @Override
    public void execute(TransferMoneyCommand command) {

        Wallet source = loadWalletPort.findById(command.sourceWalletId())
                .orElseThrow(() -> new WalletNotFoundException(command.sourceWalletId()));

        Wallet target = loadWalletPort.findById(command.targetWalletId())
                .orElseThrow(() -> new WalletNotFoundException(command.targetWalletId()));

        source.debit(command.amount());
        target.credit(command.amount());

        saveWalletPort.save(source);
        saveWalletPort.save(target);
    }

}
