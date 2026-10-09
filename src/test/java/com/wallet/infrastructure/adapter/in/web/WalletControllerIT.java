package com.wallet.infrastructure.adapter.in.web;

import com.wallet.infrastructure.adapter.in.web.dto.TransferMoneyRequest;
import com.wallet.infrastructure.adapter.out.persistence.WalletEntity;
import com.wallet.infrastructure.adapter.out.persistence.WalletJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class WalletControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WalletJpaRepository repository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        // Limpa o banco antes de cada teste para garantir isolamento
        repository.deleteAll();
    }

    @Test
    void shouldTransferMoneyEndToEnd() {
        // 1. SETUP: Criar e salvar as carteiras no banco de dados (Sem @Transactional na raiz do metodo)
        UUID sourceId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();

        WalletEntity sourceWallet = new WalletEntity(
                sourceId, "12345678901", new BigDecimal("100.00"), Instant.now());
        repository.save(sourceWallet);

        WalletEntity targetWallet = new WalletEntity(
                targetId, "98765432109", new BigDecimal("50.00"), Instant.now());
        repository.save(targetWallet);

        // 2. AÇÃO: Fazer a chamada HTTP real para o Tomcat usando o DTO
        TransferMoneyRequest request = new TransferMoneyRequest(sourceId, targetId, new BigDecimal("30.00"));

        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/api/v1/wallets/transfer",
                request,
                Void.class
        );

        // Garantir que a API retornou 200 OK (o Tomcat encontrou as carteiras criadas no Setup)
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 3. VALIDAÇÃO: Abrir uma transação isolada apenas para ler os dados com a trava pessimista
        transactionTemplate.executeWithoutResult(status -> {
            WalletEntity updatedSource = repository.findById(sourceId).orElseThrow();
            WalletEntity updatedTarget = repository.findById(targetId).orElseThrow();

            // Verifica se os R$ 30,00 saíram da carteira A (100 - 30 = 70)
            assertEquals(0, new BigDecimal("70.00").compareTo(updatedSource.getBalance()));

            // Verifica se os R$ 30,00 entraram na carteira B (50 + 30 = 80)
            assertEquals(0, new BigDecimal("80.00").compareTo(updatedTarget.getBalance()));
        });
    }
}