package com.wallet.infrastructure.config;

import com.wallet.application.port.out.LoadWalletPort;
import com.wallet.application.port.out.SaveWalletPort;
import com.wallet.application.usecase.TransferMoneyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public TransferMoneyService transferMoneyService(
            LoadWalletPort loadWalletPort,
            SaveWalletPort saveWalletPort
    ) {
        // Ensinamos o Spring a construir o nosso caso de uso puro!
        return new TransferMoneyService(loadWalletPort, saveWalletPort);

    }
}
