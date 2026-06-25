package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.infrastructure.persistence.entity.EscalaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EscalaRepository extends JpaRepository<EscalaEntity, UUID> {

    @Query("SELECT e FROM EscalaEntity e JOIN FETCH e.equipe q JOIN FETCH e.comandante c LEFT JOIN FETCH e.veiculo v WHERE e.ativa = true")
    List<EscalaEntity> findAtivas();

    @Query("SELECT e FROM EscalaEntity e JOIN FETCH e.equipe q JOIN FETCH e.comandante c LEFT JOIN FETCH e.veiculo v WHERE q.id = :equipeId AND e.ativa = true")
    List<EscalaEntity> findAtivasPorEquipe(@Param("equipeId") UUID equipeId);

    @Query("SELECT e FROM EscalaEntity e JOIN FETCH e.equipe q JOIN FETCH e.comandante c LEFT JOIN FETCH e.veiculo v LEFT JOIN FETCH e.integrantes i WHERE e.id = :id")
    Optional<EscalaEntity> findByIdFetched(@Param("id") UUID id);

    @Query(value = "SELECT e FROM EscalaEntity e WHERE e.equipe.centroComando.id = :centroComandoId",
           countQuery = "SELECT COUNT(e) FROM EscalaEntity e WHERE e.equipe.centroComando.id = :centroComandoId")
    org.springframework.data.domain.Page<EscalaEntity> findAllByCentroComandoId(@Param("centroComandoId") UUID centroComandoId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT e FROM EscalaEntity e JOIN FETCH e.equipe q JOIN FETCH e.comandante c LEFT JOIN FETCH e.veiculo v WHERE q.centroComando.id = :centroComandoId")
    List<EscalaEntity> findAllByCentroComandoIdList(@Param("centroComandoId") UUID centroComandoId);

    @Query("SELECT e FROM EscalaEntity e JOIN FETCH e.equipe q JOIN FETCH e.comandante c LEFT JOIN FETCH e.veiculo v")
    List<EscalaEntity> findAllFetched();
}
