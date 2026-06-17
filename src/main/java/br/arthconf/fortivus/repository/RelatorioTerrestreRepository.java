package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.domain.RelatorioTerrestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RelatorioTerrestreRepository extends JpaRepository<RelatorioTerrestre, Long> {
    // Como o ID do relatório é o mesmo do despacho, findById faz o mesmo papel
    default Optional<RelatorioTerrestre> findByDespachoId(Long despachoId) {
        return findById(despachoId);
    }
}
