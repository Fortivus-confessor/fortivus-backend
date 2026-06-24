package br.arthconf.fortivus.adapters.out.persistence;

import br.arthconf.fortivus.domain.RelatorioAereo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatorioAereoRepository extends JpaRepository<RelatorioAereo, Long> {
}
