package br.arthconf.fortivus.application.port.output;

import br.arthconf.fortivus.domain.model.Despacho;

import java.util.Optional;

public interface DespachoRepositoryPort {
    Despacho salvar(Despacho despacho);
    Optional<Despacho> buscarPorId(Long id);
    void deletar(Long id);
}
