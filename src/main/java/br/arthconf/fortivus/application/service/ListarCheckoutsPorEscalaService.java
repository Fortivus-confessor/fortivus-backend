package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.ListarCheckoutsPorEscalaUseCase;
import br.arthconf.fortivus.application.port.output.CheckoutEquipamentoRepositoryPort;
import br.arthconf.fortivus.domain.model.CheckoutEquipamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListarCheckoutsPorEscalaService implements ListarCheckoutsPorEscalaUseCase {

    private final CheckoutEquipamentoRepositoryPort checkoutRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<CheckoutEquipamento> executar(UUID escalaId) {
        return checkoutRepositoryPort.listarPorEscala(escalaId);
    }
}
