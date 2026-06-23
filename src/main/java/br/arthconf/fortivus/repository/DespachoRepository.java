package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.domain.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    List<Despacho> findByStatus(br.arthconf.fortivus.domain.SituacaoDespacho status);

    @org.springframework.data.jpa.repository.Query("SELECT d FROM Despacho d JOIN FETCH d.ordemServico os LEFT JOIN FETCH d.escala esc LEFT JOIN FETCH esc.equipe eq LEFT JOIN FETCH esc.comandante c LEFT JOIN FETCH esc.veiculo v WHERE d.id = :id")
    java.util.Optional<Despacho> findByIdFetched(@org.springframework.data.repository.query.Param("id") Long id);

    @org.springframework.data.jpa.repository.Query("SELECT d FROM Despacho d JOIN FETCH d.ordemServico os LEFT JOIN FETCH d.escala esc LEFT JOIN FETCH esc.equipe eq LEFT JOIN FETCH esc.comandante c LEFT JOIN FETCH esc.veiculo v")
    List<Despacho> findAllWithDetails();

    @org.springframework.data.jpa.repository.Query("SELECT MAX(d.id) FROM Despacho d WHERE d.id >= ?1 AND d.id < ?2")
    java.util.Optional<Long> findMaxIdByAno(Long minId, Long maxId);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT d FROM Despacho d JOIN FETCH d.ordemServico os LEFT JOIN FETCH d.escala esc LEFT JOIN FETCH esc.equipe eq LEFT JOIN FETCH esc.comandante c LEFT JOIN FETCH esc.veiculo v WHERE eq.centroComando.id = :centroId")
    List<Despacho> findAllByCentroComandoIdList(@org.springframework.data.repository.query.Param("centroId") java.util.UUID centroId);

    @org.springframework.data.jpa.repository.Query(value = "SELECT DISTINCT d FROM Despacho d LEFT JOIN d.escala esc LEFT JOIN esc.equipe eq WHERE eq.centroComando.id = :centroId",
           countQuery = "SELECT COUNT(DISTINCT d) FROM Despacho d LEFT JOIN d.escala esc LEFT JOIN esc.equipe eq WHERE eq.centroComando.id = :centroId")
    org.springframework.data.domain.Page<Despacho> findAllByCentroComandoId(@org.springframework.data.repository.query.Param("centroId") java.util.UUID centroId, org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT d FROM Despacho d JOIN FETCH d.ordemServico os LEFT JOIN FETCH d.escala esc LEFT JOIN FETCH esc.equipe eq LEFT JOIN FETCH esc.comandante c LEFT JOIN FETCH esc.veiculo v LEFT JOIN esc.integrantes i WHERE c.id = :usuarioId OR i.id = :usuarioId")
    List<Despacho> findAllByCombatenteIdList(@org.springframework.data.repository.query.Param("usuarioId") java.util.UUID usuarioId);

    @org.springframework.data.jpa.repository.Query(value = "SELECT DISTINCT d FROM Despacho d LEFT JOIN d.escala esc LEFT JOIN esc.comandante c LEFT JOIN esc.integrantes i WHERE c.id = :usuarioId OR i.id = :usuarioId",
           countQuery = "SELECT COUNT(DISTINCT d) FROM Despacho d LEFT JOIN d.escala esc LEFT JOIN esc.comandante c LEFT JOIN esc.integrantes i WHERE c.id = :usuarioId OR i.id = :usuarioId")
    org.springframework.data.domain.Page<Despacho> findAllByCombatenteId(@org.springframework.data.repository.query.Param("usuarioId") java.util.UUID usuarioId, org.springframework.data.domain.Pageable pageable);
}
