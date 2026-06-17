package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.domain.CentroComando;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CentroComandoRepository extends JpaRepository<CentroComando, UUID> {
    
    @Query("SELECT c FROM CentroComando c ORDER BY c.central DESC, c.nome ASC")
    List<CentroComando> findAllOrdered();
}
