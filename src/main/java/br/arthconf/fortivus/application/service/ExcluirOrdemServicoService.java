package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.ExcluirOrdemServicoUseCase;
import br.arthconf.fortivus.application.port.output.OrdemServicoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExcluirOrdemServicoService implements ExcluirOrdemServicoUseCase {

    private final OrdemServicoRepositoryPort osPort;

    @Override
    @Transactional
    public void executar(Long id) {
        if (!osPort.existe(id)) {
            throw new RuntimeException("Ordem de serviço não encontrada: " + id);
        }
        if (osPort.temDespachos(id)) {
            throw new RuntimeException("Não é possível excluir a OS pois ela possui despachos atrelados.");
        }
        osPort.deletar(id);
    }
}
