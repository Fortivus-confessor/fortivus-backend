package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.domain.model.Escala;
import br.arthconf.fortivus.infrastructure.persistence.entity.EscalaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EscalaRepositoryPort {
    Escala salvar(Escala escala, List<UUID> integrantesIds);
    Optional<Escala> buscarPorId(UUID id);
    void deletar(UUID id);
    List<Escala> listarTodas();
    List<Escala> listarAtivas();
    List<Escala> listarPorCentroComando(UUID centroId);
    Page<Escala> listarPaginado(Pageable pageable);
    Page<Escala> listarPorCentroComandoPaginado(UUID centroId, Pageable pageable);
    EscalaEntity buscarEntidadePorId(UUID id);
}
