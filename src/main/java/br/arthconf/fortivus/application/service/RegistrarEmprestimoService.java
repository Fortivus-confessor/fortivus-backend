package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.RegistrarEmprestimoUseCase;
import br.arthconf.fortivus.application.port.out.CheckoutEquipamentoRepositoryPort;
import br.arthconf.fortivus.domain.model.CheckoutEquipamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrarEmprestimoService implements RegistrarEmprestimoUseCase {

    private final CheckoutEquipamentoRepositoryPort checkoutRepositoryPort;

    @Override
    @Transactional
    public void executar(UUID escalaId, UUID equipamentoId, UUID responsavelEntregaId) {
        CheckoutEquipamento checkout = CheckoutEquipamento.builder()
                .escalaId(escalaId)
                .equipamentoId(equipamentoId)
                .responsavelEntregaId(responsavelEntregaId)
                .dataEmprestimo(LocalDateTime.now())
                .build();

        checkoutRepositoryPort.salvar(checkout);
    }
}
