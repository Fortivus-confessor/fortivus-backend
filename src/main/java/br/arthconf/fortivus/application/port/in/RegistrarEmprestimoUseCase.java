package br.arthconf.fortivus.application.port.in;

import java.util.UUID;

public interface RegistrarEmprestimoUseCase {
    void executar(UUID escalaId, UUID equipamentoId, UUID responsavelEntregaId);
}
