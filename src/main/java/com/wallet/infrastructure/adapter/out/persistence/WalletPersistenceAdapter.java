package com.wallet.infrastructure.adapter.out.persistence;

import com.wallet.application.port.out.LoadWalletPort;
import com.wallet.application.port.out.SaveWalletPort;
import com.wallet.domain.model.Wallet;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component //Transforma essa classe em um Bean gerenciado pelo Spring
public class WalletPersistenceAdapter implements LoadWalletPort, SaveWalletPort {

    private final WalletJpaRepository repository;

    public WalletPersistenceAdapter(WalletJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Wallet> findById(UUID id){
        return repository.findById(id)
                .map(this::toDomain); // Converte WalletEntity -> Wallet do Domínio
    }

    @Override
    public void save(Wallet wallet){
        WalletEntity entity = toEntity(wallet); // Converte Wallet do Domínio -> WalletEntity
        repository.save(entity);
    }

    private WalletEntity toEntity(Wallet domain) {
        return new WalletEntity(
                domain.getId(),
                domain.getDocument(),
                domain.getBalance(),
                domain.getCreatedAt()
        );
    }


    // Mappers internos simples (Domínio <-> Entidade JPA)
    private Wallet toDomain(WalletEntity entity) {
        return new Wallet(
                entity.getId(),
                entity.getDocument(),
                entity.getBalance(),
                entity.getCreatedAt()
        );
    }
}
