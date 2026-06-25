package br.arthconf.fortivus.application.port.in;

import java.util.UUID;

public interface DeletarEscalaUseCase {
    void executar(UUID id);
}
