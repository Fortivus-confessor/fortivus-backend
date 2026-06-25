package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.domain.model.CheckoutEquipamento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CheckoutEquipamentoRepositoryPort {
    CheckoutEquipamento salvar(CheckoutEquipamento checkout);
    Optional<CheckoutEquipamento> buscarPorId(UUID id);
    List<CheckoutEquipamento> listarPorEscala(UUID escalaId);
}
