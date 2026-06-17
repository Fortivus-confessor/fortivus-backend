package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.domain.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, UUID> {

    @Query("SELECT e FROM Equipamento e LEFT JOIN FETCH e.equipe")
    List<Equipamento> findAllWithEquipe();
}
