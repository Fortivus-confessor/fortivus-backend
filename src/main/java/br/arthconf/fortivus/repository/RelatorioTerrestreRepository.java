package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioTerrestreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RelatorioTerrestreRepository extends JpaRepository<RelatorioTerrestreEntity, Long> {
    default Optional<RelatorioTerrestreEntity> findByDespachoId(Long despachoId) {
        return findById(despachoId);
    }
}
