package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.BuscarOrdemServicoPorIdUseCase;
import br.arthconf.fortivus.application.port.output.OrdemServicoRepositoryPort;
import br.arthconf.fortivus.domain.model.OrdemServico;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuscarOrdemServicoPorIdService implements BuscarOrdemServicoPorIdUseCase {

    private final OrdemServicoRepositoryPort osPort;

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdemServico> executar(Long id) {
        return osPort.buscarPorId(id);
    }
}
