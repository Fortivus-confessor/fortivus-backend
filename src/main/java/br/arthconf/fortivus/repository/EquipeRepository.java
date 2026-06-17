package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.domain.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, UUID> {

    @Query("SELECT e FROM Equipe e LEFT JOIN FETCH e.centroComando")
    List<Equipe> findAllComCentro();

    @Query("SELECT e FROM Equipe e LEFT JOIN FETCH e.centroComando WHERE e.centroComando.id = :centroId")
    List<Equipe> findByCentroComandoId(UUID centroId);
}
