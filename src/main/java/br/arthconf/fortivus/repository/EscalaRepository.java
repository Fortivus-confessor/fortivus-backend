package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.domain.Escala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EscalaRepository extends JpaRepository<Escala, UUID> {
    
    @Query("SELECT e FROM Escala e JOIN FETCH e.equipe q JOIN FETCH e.comandante c LEFT JOIN FETCH e.veiculo v WHERE e.ativa = true")
    List<Escala> findAtivas();

    @Query("SELECT e FROM Escala e JOIN FETCH e.equipe q LEFT JOIN FETCH e.veiculo v WHERE q.id = :equipeId AND e.ativa = true")
    List<Escala> findAtivasPorEquipe(@Param("equipeId") UUID equipeId);

    @Query("SELECT e FROM Escala e JOIN FETCH e.equipe q JOIN FETCH e.comandante c LEFT JOIN FETCH e.veiculo v LEFT JOIN FETCH e.integrantes i WHERE e.id = :id")
    Optional<Escala> findByIdFetched(@Param("id") UUID id);

    @Query(value = "SELECT e FROM Escala e WHERE e.equipe.centroComando.id = :centroComandoId",
           countQuery = "SELECT COUNT(e) FROM Escala e WHERE e.equipe.centroComando.id = :centroComandoId")
    org.springframework.data.domain.Page<Escala> findAllByCentroComandoId(@Param("centroComandoId") UUID centroComandoId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT e FROM Escala e JOIN FETCH e.equipe q LEFT JOIN FETCH e.veiculo v WHERE q.centroComando.id = :centroComandoId")
    List<Escala> findAllByCentroComandoIdList(@Param("centroComandoId") UUID centroComandoId);
}
