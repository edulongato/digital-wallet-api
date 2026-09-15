package com.wallet.domain.model;

import com.wallet.domain.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Wallet {

    private final UUID id;
    private final String document;
    private BigDecimal balance;
    private final Instant createdAt;

    // Construtor para novas carteiras (garante saldo inicial ZERO)
    public Wallet(String document) {
        this(UUID.randomUUID(), document, BigDecimal.ZERO, Instant.now());
    }

    // Construtor completo para reconstrução
    public Wallet(UUID id, String document, BigDecimal balance, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "O ID da carteira não pode ser nulo.");
        this.document = Objects.requireNonNull(document, "O documento não pode ser nulo.");

        Objects.requireNonNull(balance, "O saldo inicial não pode ser nulo.");
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O saldo inicial não pode ser negativo.");
        }
        this.balance = balance;

        this.createdAt = Objects.requireNonNull(createdAt, "A data de criação não pode ser nula.");
    }

    public void credit(BigDecimal amount) {
        validatePositiveAmount(amount);
        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        validatePositiveAmount(amount);
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    String.format("Saldo insuficiente. Saldo disponível: R$ %s, valor solicitado: R$ %s", this.balance, amount)
            );
        }
        this.balance = this.balance.subtract(amount);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        Objects.requireNonNull(amount, "O valor da operação não pode ser nulo.");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da operação deve ser estritamente maior que zero.");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getDocument() {
        return document;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}