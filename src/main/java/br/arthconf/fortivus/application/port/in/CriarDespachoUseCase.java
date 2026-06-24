package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.Despacho;

public interface CriarDespachoUseCase {
    Despacho executar(Despacho despacho);
}
