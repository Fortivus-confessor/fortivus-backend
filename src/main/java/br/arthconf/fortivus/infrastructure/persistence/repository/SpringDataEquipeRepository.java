package br.arthconf.fortivus.infrastructure.persistence.repository;

import br.arthconf.fortivus.infrastructure.persistence.entity.EquipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataEquipeRepository extends JpaRepository<EquipeEntity, UUID> {

    @Query("SELECT e FROM EquipeEntity e LEFT JOIN FETCH e.centroComando")
    List<EquipeEntity> findAllComCentro();

    @Query("SELECT e FROM EquipeEntity e LEFT JOIN FETCH e.centroComando WHERE e.centroComando.id = :centroId")
    List<EquipeEntity> findByCentroComandoId(UUID centroId);

    org.springframework.data.domain.Page<EquipeEntity> findByCentroComandoId(UUID centroId, org.springframework.data.domain.Pageable pageable);
}
