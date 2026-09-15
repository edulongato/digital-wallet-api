package com.wallet.domain.model;

import com.wallet.domain.Wallet;
import com.wallet.domain.exception.InsufficientBalanceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class WalletTest {
    @Test
    @DisplayName("Deve iniciar uma nova carteira com saldo zero e atributos válidos.")
    public void shouldInitializeNewWalletWithZeroBalance() {
        String document = "12345678900";

        Wallet wallet = new Wallet(document);

        assertNotNull(wallet.getId());
        assertThat(wallet.getDocument()).isEqualTo(document);
        assertThat(wallet.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertNotNull(wallet.getCreatedAt());
    }

    @Test
    @DisplayName("Deve creditar valor positivo com sucesso no saldo")
    void shouldCreditAmountSuccessfully() {
        Wallet wallet = new Wallet("12345678900");
        BigDecimal creditAmount = new BigDecimal("150.75");

        wallet.credit(creditAmount);

        assertThat(wallet.getBalance()).isEqualByComparingTo("150.75");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-0.01", "-50.00"})
    @DisplayName("Deve lançar exceção ao tentar creditar valor menor ou igual a zero")
    void shouldThrowExceptionWhenCreditingInvalidAmount(String amount) {
        Wallet wallet = new Wallet("12345678900");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> wallet.credit(new BigDecimal(amount))
        );

        assertThat(exception.getMessage()).isEqualTo("O valor da operação deve ser estritamente maior que zero.");
    }

    @Test
    @DisplayName("Deve debitar valor com sucesso quando houver saldo suficiente")
    void shouldDebitAmountSuccessfullyWhenSufficientBalance() {
        Wallet wallet = new Wallet(UUID.randomUUID(), "12345678900", new BigDecimal("100.00"), Instant.now());

        wallet.debit(new BigDecimal("40.00"));

        assertThat(wallet.getBalance()).isEqualByComparingTo("60.00");
    }

    @Test
    @DisplayName("Deve debitar o valor exato esgotando o saldo sem erros")
    void shouldDebitExactBalanceAmountSuccessfully() {
        Wallet wallet = new Wallet(UUID.randomUUID(), "12345678900", new BigDecimal("50.00"), Instant.now());

        wallet.debit(new BigDecimal("50.00"));

        assertThat(wallet.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve lançar InsufficientBalanceException quando débito for maior que o saldo disponível")
    void shouldThrowExceptionWhenDebitExceedsBalance() {
        Wallet wallet = new Wallet(UUID.randomUUID(), "12345678900", new BigDecimal("30.00"), Instant.now());

        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> wallet.debit(new BigDecimal("30.01"))
        );

        assertThat(exception.getMessage()).contains("Saldo insuficiente");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-0.01", "-10.00"})
    @DisplayName("Deve lançar exceção ao tentar debitar valor menor ou igual a zero")
    void shouldThrowExceptionWhenDebitingInvalidAmount(String amount) {
        Wallet wallet = new Wallet(UUID.randomUUID(), "12345678900", new BigDecimal("100.00"), Instant.now());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> wallet.debit(new BigDecimal(amount))
        );

        assertThat(exception.getMessage()).isEqualTo("O valor da operação deve ser estritamente maior que zero.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar reconstruir carteira com saldo negativo")
    void shouldThrowExceptionWhenReconstructingWithNegativeBalance() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Wallet(UUID.randomUUID(), "12345678900", new BigDecimal("-1.00"), Instant.now())
        );
    }
}
