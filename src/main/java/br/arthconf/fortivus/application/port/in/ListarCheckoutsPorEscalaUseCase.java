package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.CheckoutEquipamento;

import java.util.List;
import java.util.UUID;

public interface ListarCheckoutsPorEscalaUseCase {
    List<CheckoutEquipamento> executar(UUID escalaId);
}
