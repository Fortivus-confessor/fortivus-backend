package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Escala;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ListarEscalasUseCase {
    List<Escala> listarTodas();
    List<Escala> listarAtivas();
    List<Escala> listarAtivasPorCentro(UUID centroId);
    List<Escala> listarPorCentroComando(UUID centroId);
    Page<Escala> listarPaginado(UUID centroComandoId, Pageable pageable);
}
