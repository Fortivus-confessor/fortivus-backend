package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.BuscarRelatorioTerrestreUseCase;
import br.arthconf.fortivus.application.port.out.RelatorioTerrestreRepositoryPort;
import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioTerrestreEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuscarRelatorioTerrestreService implements BuscarRelatorioTerrestreUseCase {

    private final RelatorioTerrestreRepositoryPort repositoryPort;

    @Override
    @Transactional(readOnly = true)
    public Optional<RelatorioTerrestreEntity> executar(Long despachoId) {
        return repositoryPort.buscarPorDespachoId(despachoId);
    }
}
