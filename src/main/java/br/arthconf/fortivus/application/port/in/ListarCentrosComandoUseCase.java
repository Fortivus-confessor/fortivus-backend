package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.CentroComando;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListarCentrosComandoUseCase {
    List<CentroComando> listarTodos();
    Page<CentroComando> listarPaginado(Pageable pageable);
}
