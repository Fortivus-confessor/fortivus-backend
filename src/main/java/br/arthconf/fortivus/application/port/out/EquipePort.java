package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.domain.model.Equipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EquipePort {
    List<Equipe> findAllComCentro();
    List<Equipe> findByCentroComandoId(UUID centroId);
    Page<Equipe> findByCentroComandoId(UUID centroId, Pageable pageable);
    Page<Equipe> findAll(Pageable pageable);
    Equipe save(Equipe equipe);
    Optional<Equipe> findById(UUID id);
    void deleteById(UUID id);
    long count();
}
