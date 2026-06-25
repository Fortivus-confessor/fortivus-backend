package br.arthconf.fortivus.application.port.in;

import java.util.UUID;

public interface DeletarEquipamentoUseCase {
    void executar(UUID id);
}
