package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.domain.model.CentroComando;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CentroComandoRepositoryPort {
    CentroComando salvar(CentroComando centroComando);
    Optional<CentroComando> buscarPorId(UUID id);
    void deletar(UUID id);
    List<CentroComando> listarTodos();
    Page<CentroComando> listarPaginado(Pageable pageable);
}
