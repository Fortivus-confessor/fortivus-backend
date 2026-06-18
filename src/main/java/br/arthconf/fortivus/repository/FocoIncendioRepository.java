package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.domain.FocoIncendio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FocoIncendioRepository extends JpaRepository<FocoIncendio, UUID> {
    List<FocoIncendio> findByStatusAndDataHoraDeteccaoAfter(String status, LocalDateTime dataHora);
    List<FocoIncendio> findByOrigemRegistro(String origemRegistro);
    boolean existsByCodigoInpe(String codigoInpe);
    Optional<FocoIncendio> findByCodigoInpe(String codigoInpe);
}
