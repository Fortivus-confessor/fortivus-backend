package br.arthconf.fortivus.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import br.arthconf.fortivus.dto.DespachoDTO;
import java.util.List;

public interface ListarDespachosUseCase {
    List<DespachoDTO> listarTodos();
    Page<DespachoDTO> listarPaginado(Pageable pageable);
}
