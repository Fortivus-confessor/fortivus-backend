package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Usuario;

import java.util.UUID;

public interface GerenciarUsuarioUseCase {
    Usuario buscarPorId(UUID id);
    Usuario salvar(Usuario usuario);
    void deletar(UUID id);
}
