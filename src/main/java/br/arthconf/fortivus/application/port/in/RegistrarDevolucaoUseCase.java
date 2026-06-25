package br.arthconf.fortivus.application.port.in;

import java.util.UUID;

public interface RegistrarDevolucaoUseCase {
    void executar(UUID checkoutId, UUID responsavelRecebimentoId);
}
