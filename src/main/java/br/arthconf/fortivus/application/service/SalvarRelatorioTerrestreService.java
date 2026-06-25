package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.SalvarRelatorioTerrestreUseCase;
import br.arthconf.fortivus.application.port.out.RelatorioTerrestreRepositoryPort;
import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioTerrestreEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalvarRelatorioTerrestreService implements SalvarRelatorioTerrestreUseCase {

    private final RelatorioTerrestreRepositoryPort repositoryPort;

    @Override
    @Transactional
    public RelatorioTerrestreEntity executar(RelatorioTerrestreEntity relatorio) {
        return repositoryPort.salvar(relatorio);
    }
}
