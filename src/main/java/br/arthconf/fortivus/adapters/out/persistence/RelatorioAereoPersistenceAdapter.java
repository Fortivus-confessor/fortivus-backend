package br.arthconf.fortivus.adapters.out.persistence;

import br.arthconf.fortivus.application.port.out.RelatorioAereoPort;
import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioAereoEntity;
import br.arthconf.fortivus.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RelatorioAereoPersistenceAdapter implements RelatorioAereoPort {

    private final RelatorioAereoRepository repository;
    private final DespachoRepository despachoRepository;

    @Override
    @Transactional
    public RelatorioAereoEntity salvar(RelatorioAereoEntity relatorio) {
        Long id = relatorio.getId() != null ? relatorio.getId()
                : (relatorio.getDespacho() != null ? relatorio.getDespacho().getId() : null);
        DespachoEntity managed = despachoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despacho não encontrado: " + id));
        relatorio.setDespacho(managed);
        relatorio.setId(managed.getId());
        return repository.save(relatorio);
    }

    @Override
    public Optional<RelatorioAereoEntity> buscarPorDespachoId(Long despachoId) {
        return repository.findById(despachoId);
    }
}
