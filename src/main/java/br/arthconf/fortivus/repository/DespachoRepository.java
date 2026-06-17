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
}
