package br.arthconf.fortivus.adapters.out.persistence;

import br.arthconf.fortivus.domain.RelatorioMaquinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatorioMaquinarioRepository extends JpaRepository<RelatorioMaquinario, Long> {
}
