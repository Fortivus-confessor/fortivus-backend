package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.DeletarDespachoUseCase;
import br.arthconf.fortivus.application.port.output.DespachoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeletarDespachoService implements DeletarDespachoUseCase {

    private final DespachoRepositoryPort despachoPort;

    @Override
    @Transactional
    public void executar(Long id) {
        despachoPort.deletar(id);
    }
}
