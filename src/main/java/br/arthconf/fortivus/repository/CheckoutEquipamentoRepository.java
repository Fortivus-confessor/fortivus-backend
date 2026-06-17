package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.domain.CheckoutEquipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CheckoutEquipamentoRepository extends JpaRepository<CheckoutEquipamento, UUID> {
}
