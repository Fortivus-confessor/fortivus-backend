package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Equipe;

import java.util.UUID;

public interface GerenciarEquipeUseCase {
    Equipe buscarPorId(UUID id);
    Equipe salvar(Equipe equipe);
    void deletar(UUID id);
}
