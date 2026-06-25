package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.EditarOrdemServicoUseCase;
import br.arthconf.fortivus.application.port.out.OrdemServicoRepositoryPort;
import br.arthconf.fortivus.domain.model.OrdemServico;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditarOrdemServicoService implements EditarOrdemServicoUseCase {

    private final OrdemServicoRepositoryPort osPort;

    @Override
    @Transactional
    public OrdemServico executar(Command cmd) {
        OrdemServico os = osPort.buscarPorId(cmd.id())
                .orElseThrow(() -> new RuntimeException("Ordem de serviço não encontrada: " + cmd.id()));
        os.setDescricaoTarefa(cmd.descricaoTarefa());
        os.setEventoFogoId(cmd.eventoFogoId());
        return osPort.salvar(os);
    }
}
