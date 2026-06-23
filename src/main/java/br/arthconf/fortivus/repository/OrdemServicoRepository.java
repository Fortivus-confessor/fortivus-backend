package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.domain.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    @Query("SELECT o FROM OrdemServico o JOIN FETCH o.escala e JOIN FETCH e.equipe q LEFT JOIN FETCH e.veiculo v JOIN FETCH o.relator u")
    List<OrdemServico> findAllFetched();

    @Query("SELECT o FROM OrdemServico o JOIN FETCH o.escala e JOIN FETCH e.equipe q LEFT JOIN FETCH e.veiculo v JOIN FETCH o.relator u LEFT JOIN FETCH o.despachos d LEFT JOIN FETCH d.escala de LEFT JOIN FETCH de.equipe dq LEFT JOIN FETCH de.comandante dc WHERE o.id = :id")
    Optional<OrdemServico> findByIdFetched(@Param("id") Long id);

    @Query("SELECT MAX(o.id) FROM OrdemServico o WHERE o.id >= ?1 AND o.id < ?2")
    Optional<Long> findMaxIdByAno(Long minId, Long maxId);

    @Query("SELECT DISTINCT o FROM OrdemServico o WHERE o.escala.equipe.centroComando.id = :centroId")
    List<OrdemServico> findAllByCentroComandoIdList(@Param("centroId") java.util.UUID centroId);

    @Query(value = "SELECT DISTINCT o FROM OrdemServico o WHERE o.escala.equipe.centroComando.id = :centroId",
           countQuery = "SELECT COUNT(DISTINCT o) FROM OrdemServico o WHERE o.escala.equipe.centroComando.id = :centroId")
    org.springframework.data.domain.Page<OrdemServico> findAllByCentroComandoId(@Param("centroId") java.util.UUID centroId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT DISTINCT o FROM OrdemServico o JOIN o.despachos d JOIN d.escala e LEFT JOIN e.integrantes i WHERE e.comandante.id = :usuarioId OR i.id = :usuarioId")
    List<OrdemServico> findAllByCombatenteIdList(@Param("usuarioId") java.util.UUID usuarioId);

    @Query(value = "SELECT DISTINCT o FROM OrdemServico o JOIN o.despachos d JOIN d.escala e LEFT JOIN e.integrantes i WHERE e.comandante.id = :usuarioId OR i.id = :usuarioId",
           countQuery = "SELECT COUNT(DISTINCT o) FROM OrdemServico o JOIN o.despachos d JOIN d.escala e LEFT JOIN e.integrantes i WHERE e.comandante.id = :usuarioId OR i.id = :usuarioId")
    org.springframework.data.domain.Page<OrdemServico> findAllByCombatenteId(@Param("usuarioId") java.util.UUID usuarioId, org.springframework.data.domain.Pageable pageable);
}
