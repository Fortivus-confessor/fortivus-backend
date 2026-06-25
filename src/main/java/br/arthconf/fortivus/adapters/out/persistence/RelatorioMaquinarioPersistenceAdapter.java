package br.arthconf.fortivus.adapters.out.persistence;

import br.arthconf.fortivus.application.port.out.RelatorioMaquinarioPort;
import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioMaquinarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RelatorioMaquinarioPersistenceAdapter implements RelatorioMaquinarioPort {

    private final RelatorioMaquinarioRepository repository;

    @Override
    public RelatorioMaquinarioEntity salvar(RelatorioMaquinarioEntity relatorio) {
        return repository.save(relatorio);
    }

    @Override
    public Optional<RelatorioMaquinarioEntity> buscarPorDespachoId(Long despachoId) {
        return repository.findById(despachoId);
    }
}
