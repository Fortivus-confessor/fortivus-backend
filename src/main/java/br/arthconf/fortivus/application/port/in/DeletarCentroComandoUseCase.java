package br.arthconf.fortivus.application.port.in;

import java.util.UUID;

public interface DeletarCentroComandoUseCase {
    void executar(UUID id);
}
