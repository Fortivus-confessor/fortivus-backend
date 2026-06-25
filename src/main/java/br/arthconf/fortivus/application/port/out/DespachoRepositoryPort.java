package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.domain.model.Despacho;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DespachoRepositoryPort {
    Despacho salvar(Despacho despacho);
    Optional<Despacho> buscarPorId(Long id);
    void deletar(Long id);
    List<Despacho> listarTodas();
    List<Despacho> listarPorCentroComando(UUID centroId);
    List<Despacho> listarPorCombatente(UUID usuarioId);
    Page<Despacho> listarPaginado(Pageable pageable);
    Page<Despacho> listarPorCentroComandoPaginado(UUID centroId, Pageable pageable);
    Page<Despacho> listarPorCombatentePaginado(UUID usuarioId, Pageable pageable);
    boolean pertenceAoDespacho(Long despachoId, UUID usuarioId);
}
