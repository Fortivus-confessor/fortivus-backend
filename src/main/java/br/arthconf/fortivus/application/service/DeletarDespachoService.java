package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.output.DespachoRepositoryPort;
import br.arthconf.fortivus.application.usecase.DeletarDespachoUseCase;
import br.arthconf.fortivus.repository.RelatorioTerrestreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeletarDespachoService implements DeletarDespachoUseCase {

    private final DespachoRepositoryPort despachoPort;
    private final RelatorioTerrestreRepository relatorioTerrestreRepository;

    @Override
    @Transactional
    public void executar(Long id) {
        if (relatorioTerrestreRepository.existsById(id)) {
            relatorioTerrestreRepository.deleteById(id);
        }
        despachoPort.deletar(id);
    }
}
