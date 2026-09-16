package com.wallet.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity // Diz ao Hibernate que isso mapeia uma tabela
@Table(name = "wallets") // Nome da tabela no banco
public class WalletEntity {

    @Id // Chave primária
    private UUID id;

    @Column(nullable = false, unique = true, length = 14)
    private String document;

    @Column(nullable = false, precision = 19, scale = 2) // 2 casas decimais (dinheiro)
    private BigDecimal balance;

    @Column(nullable = false, updatable = false) // Não permite alterar a data de criação
    private Instant createdAt;

    // Construtor vazio obrigatório para o JPA/Hibernate
    protected WalletEntity() {

    }

    // Construtor para transformar Domínio -> Entity
    public WalletEntity(UUID id, String document, BigDecimal balance, Instant createdAt) {
        this.id = id;
        this.document = document;
        this.balance = balance;
        this.createdAt = createdAt;

    }

    // Getters
    public UUID getId() {return id;}
    public String getDocument() {return document;}
    public BigDecimal getBalance() {return balance;}
    public Instant getCreatedAt() {return createdAt;}

}
