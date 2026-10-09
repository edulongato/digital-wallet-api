package com.wallet.application.usecase;

import com.wallet.application.port.in.TransferMoneyCommand;
import com.wallet.application.port.out.LoadWalletPort;
import com.wallet.application.port.out.SaveWalletPort;
import com.wallet.domain.model.Wallet;
import com.wallet.domain.exception.InsufficientBalanceException;
import com.wallet.domain.exception.WalletNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TransferMoneyServiceTest {

    @Mock
    private LoadWalletPort loadWalletPort;

    @Mock
    private SaveWalletPort saveWalletPort;

    @InjectMocks
    private TransferMoneyService transferMoneyService;

    @Test
    @DisplayName("Deve realizar transferência com sucesso e salvar o estado de ambas as carteiras")
    void shouldTransferMoneySuccessfully() {
        // Arrange
        UUID sourceId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        BigDecimal transferAmount = new BigDecimal("50.00");

        Wallet sourceWallet = new Wallet(sourceId, "111", new BigDecimal("100.00"), Instant.now());
        Wallet targetWallet = new Wallet(targetId, "222", new BigDecimal("50.00"), Instant.now());

        TransferMoneyCommand command = new TransferMoneyCommand(sourceId, targetId, transferAmount);

        // BDD Mockito syntax: chamando o metodo NA instância do mock
        given(loadWalletPort.findById(sourceId)).willReturn(Optional.of(sourceWallet));
        given(loadWalletPort.findById(targetId)).willReturn(Optional.of(targetWallet));

        // Act
        transferMoneyService.execute(command);

        // Assert
        assertThat(sourceWallet.getBalance()).isEqualByComparingTo("50.00");
        assertThat(targetWallet.getBalance()).isEqualByComparingTo("100.00");

        then(saveWalletPort).should(times(1)).save(sourceWallet);
        then(saveWalletPort).should(times(1)).save(targetWallet);
    }

    @Test
    @DisplayName("Deve lançar WalletNotFoundException se a carteira de origem não existir")
    void shouldThrowExceptionWhenSourceWalletNotFound() {
        UUID sourceId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        TransferMoneyCommand command = new TransferMoneyCommand(sourceId, targetId, new BigDecimal("50.00"));

        given(loadWalletPort.findById(sourceId)).willReturn(Optional.empty());

        WalletNotFoundException exception = assertThrows(
                WalletNotFoundException.class,
                () -> transferMoneyService.execute(command)
        );

        assertThat(exception.getMessage()).contains(sourceId.toString());

        then(loadWalletPort).should(never()).findById(targetId);
        then(saveWalletPort).should(never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar InsufficientBalanceException se a carteira de origem não tiver saldo")
    void shouldThrowExceptionWhenSourceHasInsufficientBalance() {
        UUID sourceId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        BigDecimal transferAmount = new BigDecimal("100.00");

        Wallet sourceWallet = new Wallet(sourceId, "111", new BigDecimal("30.00"), Instant.now());
        Wallet targetWallet = new Wallet(targetId, "222", new BigDecimal("50.00"), Instant.now());

        TransferMoneyCommand command = new TransferMoneyCommand(sourceId, targetId, transferAmount);

        given(loadWalletPort.findById(sourceId)).willReturn(Optional.of(sourceWallet));
        given(loadWalletPort.findById(targetId)).willReturn(Optional.of(targetWallet));

        assertThrows(
                InsufficientBalanceException.class,
                () -> transferMoneyService.execute(command)
        );

        then(saveWalletPort).should(never()).save(any());
    }
}