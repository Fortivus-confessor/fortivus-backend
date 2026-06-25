package br.arthconf.fortivus.infrastructure.persistence.adapter;

import br.arthconf.fortivus.application.port.out.RelatorioTerrestreRepositoryPort;
import br.arthconf.fortivus.domain.AnexoRelatorio;
import br.arthconf.fortivus.domain.PropriedadeRelatorio;
import br.arthconf.fortivus.domain.RelatorioTerrestre;
import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.repository.DespachoRepository;
import br.arthconf.fortivus.repository.RelatorioTerrestreRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RelatorioTerrestrePersistenceAdapter implements RelatorioTerrestreRepositoryPort {

    private final RelatorioTerrestreRepository repository;
    private final DespachoRepository despachoRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<RelatorioTerrestre> buscarPorDespachoId(Long despachoId) {
        Optional<RelatorioTerrestre> opt = repository.findByDespachoId(despachoId);
        opt.ifPresent(this::inicializarColecoes);
        return opt;
    }

    @Override
    @Transactional
    public RelatorioTerrestre salvar(RelatorioTerrestre relatorio) {
        Long id = relatorio.getDespacho().getId();

        DespachoEntity despachoGerenciado = despachoRepository.findByIdFetched(id).orElse(null);
        relatorio.setDespacho(despachoGerenciado);

        RelatorioTerrestre persistente = repository.findById(id).orElse(null);

        if (persistente == null) {
            relatorio.setId(id);
            relatorio.setDataInicio(relatorio.getDespacho().getDataInicio());
            if (relatorio.getDataFim() == null) {
                relatorio.setDataFim(LocalDateTime.now());
            }
            if (relatorio.getPropriedades() != null) {
                relatorio.getPropriedades().forEach(p -> p.setRelatorio(relatorio));
            }
            if (relatorio.getAnexos() != null) {
                relatorio.getAnexos().forEach(a -> a.setRelatorio(relatorio));
            }
            persistente = repository.save(relatorio);
        } else {
            persistente.setAcoesRealizadas(relatorio.getAcoesRealizadas());
            persistente.setOrgaosApoio(relatorio.getOrgaosApoio());
            persistente.setOutrosOrgaosDescricao(relatorio.getOutrosOrgaosDescricao());
            persistente.setAreaAtuacaoGeom(relatorio.getAreaAtuacaoGeom());
            persistente.setHouveUsoAgua(relatorio.getHouveUsoAgua());
            persistente.setVolumeAguaLitros(relatorio.getVolumeAguaLitros());
            persistente.setOrigensAgua(relatorio.getOrigensAgua());
            persistente.setOutraOrigemAguaDescricao(relatorio.getOutraOrigemAguaDescricao());
            persistente.setHouveApoioPropriedades(relatorio.getHouveApoioPropriedades());
            persistente.setHouveRecusaPropriedades(relatorio.getHouveRecusaPropriedades());
            persistente.setPossivelOrigemIncendio(relatorio.getPossivelOrigemIncendio());
            persistente.setEfetividadeCombate(relatorio.getEfetividadeCombate());
            persistente.setNecessidadeReforco(relatorio.getNecessidadeReforco());
            persistente.setTiposReforcoNecessarios(relatorio.getTiposReforcoNecessarios());
            persistente.setHistoricoDescritivo(relatorio.getHistoricoDescritivo());
            persistente.setResultadoOcorrencia(relatorio.getResultadoOcorrencia());
            persistente.setOutroResultadoDescricao(relatorio.getOutroResultadoDescricao());
            persistente.setDataFim(LocalDateTime.now());
            updatePropriedades(persistente, relatorio.getPropriedades());
            updateAnexos(persistente, relatorio.getAnexos());
            persistente = repository.save(persistente);
        }

        var despachoEntity = persistente.getDespacho();
        despachoEntity.setStatus(br.arthconf.fortivus.domain.SituacaoDespacho.CONCLUIDO);
        despachoEntity.setDataFim(persistente.getDataFim());

        inicializarColecoes(persistente);
        return persistente;
    }

    private void inicializarColecoes(RelatorioTerrestre relatorio) {
        Hibernate.initialize(relatorio.getAcoesRealizadas());
        Hibernate.initialize(relatorio.getOrgaosApoio());
        Hibernate.initialize(relatorio.getOrigensAgua());
        Hibernate.initialize(relatorio.getTiposReforcoNecessarios());
        Hibernate.initialize(relatorio.getAnexos());
        Hibernate.initialize(relatorio.getPropriedades());
        if (relatorio.getDespacho() != null) {
            Hibernate.initialize(relatorio.getDespacho());
        }
    }

    private void updatePropriedades(RelatorioTerrestre destino, java.util.List<PropriedadeRelatorio> novos) {
        destino.getPropriedades().clear();
        if (novos != null) {
            for (var n : novos) {
                n.setRelatorio(destino);
                destino.getPropriedades().add(n);
            }
        }
    }

    private void updateAnexos(RelatorioTerrestre destino, java.util.List<AnexoRelatorio> novos) {
        destino.getAnexos().clear();
        if (novos != null) {
            for (var n : novos) {
                n.setRelatorio(destino);
                destino.getAnexos().add(n);
            }
        }
    }
}
