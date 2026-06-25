package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.DeletarCentroComandoUseCase;
import br.arthconf.fortivus.application.port.out.CentroComandoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletarCentroComandoService implements DeletarCentroComandoUseCase {

    private final CentroComandoRepositoryPort centroPort;

    @Override
    @Transactional
    public void executar(UUID id) {
        centroPort.deletar(id);
    }
}
