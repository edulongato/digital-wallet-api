package com.wallet.infrastructure.adapter.in.web;

import com.wallet.application.port.in.TransferMoneyCommand;
import com.wallet.application.port.in.TransferMoneyUseCase;
import com.wallet.infrastructure.adapter.in.web.dto.TransferMoneyRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    // Dependemos apenas da Interface (Porta de Entrada)
    private final TransferMoneyUseCase transferMoneyUseCase;

    public WalletController(TransferMoneyUseCase transferMoneyUseCase) {
        this.transferMoneyUseCase = transferMoneyUseCase;
    }

    @Transactional
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferMoneyRequest request){
        // 1. Converte o DTO da Web para o Comando da Aplicação
        TransferMoneyCommand command = new TransferMoneyCommand(
                request.sourceWalletId(),
                request.targetWalletId(),
                request.amount()
        );

        // 2. Executa o caso de uso
        transferMoneyUseCase.execute(command);

        // 3. Retorna 200 OK se tudo der certo
        return ResponseEntity.ok().build();
    }


}
