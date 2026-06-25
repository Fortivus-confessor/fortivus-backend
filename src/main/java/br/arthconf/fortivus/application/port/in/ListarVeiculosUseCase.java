package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListarVeiculosUseCase {
    List<Veiculo> listarTodos();
    Page<Veiculo> listarPaginado(Pageable pageable);
}
