package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.ListarEscalasUseCase;
import br.arthconf.fortivus.application.port.out.EscalaRepositoryPort;
import br.arthconf.fortivus.domain.model.Escala;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListarEscalasService implements ListarEscalasUseCase {

    private final EscalaRepositoryPort escalaRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<Escala> listarTodas() {
        return escalaRepositoryPort.listarTodas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Escala> listarAtivas() {
        return escalaRepositoryPort.listarAtivas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Escala> listarAtivasPorCentro(UUID centroComandoId) {
        return escalaRepositoryPort.listarAtivas().stream()
                .filter(e -> centroComandoId.equals(e.getCentroComandoId()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Escala> listarPorCentroComando(UUID centroId) {
        return escalaRepositoryPort.listarPorCentroComando(centroId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Escala> listarPaginado(UUID centroComandoId, Pageable pageable) {
        if (centroComandoId != null) {
            return escalaRepositoryPort.listarPorCentroComandoPaginado(centroComandoId, pageable);
        }
        return escalaRepositoryPort.listarPaginado(pageable);
    }
}
