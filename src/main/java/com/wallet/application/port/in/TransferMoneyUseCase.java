package com.wallet.application.port.in;

public interface TransferMoneyUseCase {
    void execute(TransferMoneyCommand command);
}
