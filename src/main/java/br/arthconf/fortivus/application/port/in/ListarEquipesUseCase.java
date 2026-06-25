package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Equipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ListarEquipesUseCase {
    List<Equipe> listarTodas();
    List<Equipe> buscarPorCentro(UUID centroId);
    Page<Equipe> listarPaginado(Pageable pageable);
}
