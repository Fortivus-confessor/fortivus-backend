package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.OrdemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListarOrdensServicoUseCase {
    List<OrdemServico> listarTodas();
    Page<OrdemServico> listarPaginado(Pageable pageable);
}
