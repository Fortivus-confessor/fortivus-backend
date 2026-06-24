package br.arthconf.fortivus.adapters.out.persistence;

import br.arthconf.fortivus.application.port.out.RelatorioMaquinarioPort;
import br.arthconf.fortivus.domain.RelatorioMaquinario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RelatorioMaquinarioPersistenceAdapter implements RelatorioMaquinarioPort {

    private final RelatorioMaquinarioRepository repository;

    @Override
    public RelatorioMaquinario salvar(RelatorioMaquinario relatorio) {
        return repository.save(relatorio);
    }

    @Override
    public Optional<RelatorioMaquinario> buscarPorDespachoId(Long despachoId) {
        return repository.findById(despachoId);
    }
}
