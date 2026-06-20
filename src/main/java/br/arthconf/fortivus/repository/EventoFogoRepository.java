package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.domain.EventoFogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventoFogoRepository extends JpaRepository<EventoFogo, UUID> {
    List<EventoFogo> findByCodigoContainingIgnoreCase(String codigo);
}
