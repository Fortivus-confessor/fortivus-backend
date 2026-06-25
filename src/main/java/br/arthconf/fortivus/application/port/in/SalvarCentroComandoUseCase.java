package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.CentroComando;

public interface SalvarCentroComandoUseCase {
    CentroComando executar(CentroComando centroComando);
}
