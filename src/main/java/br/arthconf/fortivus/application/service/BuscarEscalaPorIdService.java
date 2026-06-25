package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.BuscarEscalaPorIdUseCase;
import br.arthconf.fortivus.application.port.output.EscalaRepositoryPort;
import br.arthconf.fortivus.domain.model.Escala;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarEscalaPorIdService implements BuscarEscalaPorIdUseCase {

    private final EscalaRepositoryPort escalaRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public Optional<Escala> executar(UUID id) {
        return escalaRepositoryPort.buscarPorId(id);
    }
}
