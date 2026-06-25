package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.RegistrarDevolucaoUseCase;
import br.arthconf.fortivus.application.port.out.CheckoutEquipamentoRepositoryPort;
import br.arthconf.fortivus.domain.model.CheckoutEquipamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrarDevolucaoService implements RegistrarDevolucaoUseCase {

    private final CheckoutEquipamentoRepositoryPort checkoutRepositoryPort;

    @Override
    @Transactional
    public void executar(UUID checkoutId, UUID responsavelRecebimentoId) {
        CheckoutEquipamento checkout = checkoutRepositoryPort.buscarPorId(checkoutId)
                .orElseThrow(() -> new RuntimeException("Registro de checkout não encontrado: " + checkoutId));

        CheckoutEquipamento atualizado = CheckoutEquipamento.builder()
                .id(checkout.getId())
                .escalaId(checkout.getEscalaId())
                .equipamentoId(checkout.getEquipamentoId())
                .responsavelEntregaId(checkout.getResponsavelEntregaId())
                .dataEmprestimo(checkout.getDataEmprestimo())
                .dataDevolucao(LocalDateTime.now())
                .responsavelRecebimentoId(responsavelRecebimentoId)
                .build();

        checkoutRepositoryPort.salvar(atualizado);
    }
}
