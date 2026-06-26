package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.domain.model.Despacho;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ListarDespachosUseCase {
    List<Despacho> listarTodos();
    Page<Despacho> listarPaginado(Pageable pageable);

    /** Mobile: despachos onde o usuário é o responsável direto, com filtro opcional de status. */
    Page<Despacho> listarMeusPaginado(UUID responsavelId, List<SituacaoDespacho> statuses, Pageable pageable);
}
