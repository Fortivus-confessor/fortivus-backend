package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.BuscarCentroComandoPorIdUseCase;
import br.arthconf.fortivus.application.port.out.CentroComandoRepositoryPort;
import br.arthconf.fortivus.domain.model.CentroComando;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarCentroComandoPorIdService implements BuscarCentroComandoPorIdUseCase {

    private final CentroComandoRepositoryPort centroPort;

    @Override
    @Transactional(readOnly = true)
    public Optional<CentroComando> executar(UUID id) {
        return centroPort.buscarPorId(id);
    }
}
