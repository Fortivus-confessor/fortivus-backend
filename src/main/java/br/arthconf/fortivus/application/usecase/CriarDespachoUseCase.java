package br.arthconf.fortivus.application.usecase;

import br.arthconf.fortivus.domain.model.Despacho;

public interface CriarDespachoUseCase {
    Despacho executar(Despacho despacho);
}
