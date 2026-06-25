package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.infrastructure.persistence.entity.EquipamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EquipamentoRepository extends JpaRepository<EquipamentoEntity, UUID> {

    @Query("SELECT e FROM EquipamentoEntity e LEFT JOIN FETCH e.equipe")
    List<EquipamentoEntity> findAllWithEquipe();
}
