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
}
