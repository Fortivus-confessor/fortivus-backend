package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.infrastructure.persistence.entity.CentroComandoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CentroComandoRepository extends JpaRepository<CentroComandoEntity, UUID> {

    @Query("SELECT c FROM CentroComandoEntity c ORDER BY c.central DESC, c.nome ASC")
    List<CentroComandoEntity> findAllOrdered();
}
