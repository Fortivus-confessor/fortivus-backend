package br.arthconf.fortivus.adapters.out.persistence;

import br.arthconf.fortivus.application.port.out.RelatorioAereoPort;
import br.arthconf.fortivus.domain.RelatorioAereo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RelatorioAereoPersistenceAdapter implements RelatorioAereoPort {

    private final RelatorioAereoRepository repository;

    @Override
    public RelatorioAereo salvar(RelatorioAereo relatorio) {
        return repository.save(relatorio);
    }

    @Override
    public Optional<RelatorioAereo> buscarPorDespachoId(Long despachoId) {
        return repository.findById(despachoId);
    }
}
