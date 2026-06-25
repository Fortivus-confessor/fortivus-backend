package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Usuario;

public interface ObterUsuarioLogadoUseCase {
    Usuario getUsuarioLogado();
}
