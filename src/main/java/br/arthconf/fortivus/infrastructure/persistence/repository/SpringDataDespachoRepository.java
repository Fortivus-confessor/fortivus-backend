package br.arthconf.fortivus.infrastructure.persistence.repository;

import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataDespachoRepository extends JpaRepository<DespachoEntity, Long> {

    List<DespachoEntity> findByStatus(SituacaoDespacho status);

    @Query("SELECT d FROM DespachoEntity d JOIN FETCH d.ordemServico os LEFT JOIN FETCH d.escala esc LEFT JOIN FETCH esc.equipe eq LEFT JOIN FETCH esc.comandante c LEFT JOIN FETCH esc.veiculo v WHERE d.id = :id")
    Optional<DespachoEntity> findByIdFetched(@Param("id") Long id);

    @Query("SELECT d FROM DespachoEntity d JOIN FETCH d.ordemServico os LEFT JOIN FETCH d.escala esc LEFT JOIN FETCH esc.equipe eq LEFT JOIN FETCH esc.comandante c LEFT JOIN FETCH esc.veiculo v")
    List<DespachoEntity> findAllWithDetails();

    @Query("SELECT MAX(d.id) FROM DespachoEntity d WHERE d.id >= ?1 AND d.id < ?2")
    Optional<Long> findMaxIdByAno(Long minId, Long maxId);

    @Query("SELECT DISTINCT d FROM DespachoEntity d JOIN FETCH d.ordemServico os LEFT JOIN FETCH d.escala esc LEFT JOIN FETCH esc.equipe eq LEFT JOIN FETCH esc.comandante c LEFT JOIN FETCH esc.veiculo v WHERE eq.centroComando.id = :centroId")
    List<DespachoEntity> findAllByCentroComandoIdList(@Param("centroId") UUID centroId);

    @Query(value = "SELECT DISTINCT d FROM DespachoEntity d LEFT JOIN d.escala esc LEFT JOIN esc.equipe eq WHERE eq.centroComando.id = :centroId",
           countQuery = "SELECT COUNT(DISTINCT d) FROM DespachoEntity d LEFT JOIN d.escala esc LEFT JOIN esc.equipe eq WHERE eq.centroComando.id = :centroId")
    Page<DespachoEntity> findAllByCentroComandoId(@Param("centroId") UUID centroId, Pageable pageable);

    @Query("SELECT DISTINCT d FROM DespachoEntity d JOIN FETCH d.ordemServico os LEFT JOIN FETCH d.escala esc LEFT JOIN FETCH esc.equipe eq LEFT JOIN FETCH esc.comandante c LEFT JOIN FETCH esc.veiculo v LEFT JOIN esc.integrantes i WHERE c.id = :usuarioId OR i.id = :usuarioId")
    List<DespachoEntity> findAllByCombatenteIdList(@Param("usuarioId") UUID usuarioId);

    @Query(value = "SELECT DISTINCT d FROM DespachoEntity d LEFT JOIN d.escala esc LEFT JOIN esc.comandante c LEFT JOIN esc.integrantes i WHERE c.id = :usuarioId OR i.id = :usuarioId",
           countQuery = "SELECT COUNT(DISTINCT d) FROM DespachoEntity d LEFT JOIN d.escala esc LEFT JOIN esc.comandante c LEFT JOIN esc.integrantes i WHERE c.id = :usuarioId OR i.id = :usuarioId")
    Page<DespachoEntity> findAllByCombatenteId(@Param("usuarioId") UUID usuarioId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END FROM DespachoEntity d LEFT JOIN d.escala esc LEFT JOIN esc.comandante c LEFT JOIN esc.integrantes i WHERE d.id = :despachoId AND (c.id = :usuarioId OR i.id = :usuarioId)")
    boolean pertenceAoDespacho(@Param("despachoId") Long despachoId, @Param("usuarioId") UUID usuarioId);

    Page<DespachoEntity> findByResponsavelId(UUID responsavelId, Pageable pageable);

    Page<DespachoEntity> findByResponsavelIdAndStatusIn(UUID responsavelId, List<SituacaoDespacho> statuses, Pageable pageable);
}
