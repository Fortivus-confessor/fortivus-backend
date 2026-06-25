package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ListarUsuariosUseCase {
    List<Usuario> listarTodos();
    List<Usuario> buscarPorCentro(UUID centroId);
    Page<Usuario> listarPaginado(Pageable pageable);
}
