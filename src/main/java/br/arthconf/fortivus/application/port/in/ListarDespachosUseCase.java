package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Despacho;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListarDespachosUseCase {
    List<Despacho> listarTodos();
    Page<Despacho> listarPaginado(Pageable pageable);
}
