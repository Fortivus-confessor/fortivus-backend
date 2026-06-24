package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.AnexoRelatorio;
import br.arthconf.fortivus.domain.PropriedadeRelatorio;
import br.arthconf.fortivus.domain.RelatorioTerrestre;
import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.repository.RelatorioTerrestreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RelatorioTerrestreService {
    
    private final RelatorioTerrestreRepository repository;
    private final DespachoService despachoService;

    @Transactional(readOnly = true)
    public RelatorioTerrestre buscarPorDespachoId(Long despachoId) {
        RelatorioTerrestre relatorio = repository.findById(despachoId).orElse(null);
        if (relatorio != null) {
            inicializarColecoes(relatorio);
        }
        return relatorio;
    }

    private void inicializarColecoes(RelatorioTerrestre relatorio) {
        org.hibernate.Hibernate.initialize(relatorio.getAcoesRealizadas());
        org.hibernate.Hibernate.initialize(relatorio.getOrgaosApoio());
        org.hibernate.Hibernate.initialize(relatorio.getOrigensAgua());
        org.hibernate.Hibernate.initialize(relatorio.getTiposReforcoNecessarios());
        org.hibernate.Hibernate.initialize(relatorio.getAnexos());
        org.hibernate.Hibernate.initialize(relatorio.getPropriedades());
        // Inicializa o despacho para serialização
        if (relatorio.getDespacho() != null) {
            org.hibernate.Hibernate.initialize(relatorio.getDespacho());
        }
    }

    @Transactional
    public RelatorioTerrestre salvar(RelatorioTerrestre relatorio) {
        Long id = relatorio.getDespacho().getId();
        
        // Garante que o Despacho está atrelado à sessão do Hibernate (Managed)
        br.arthconf.fortivus.domain.Despacho despachoGerenciado = despachoService.buscarPorId(id);
        relatorio.setDespacho(despachoGerenciado);
        
        // Busca a instância gerenciada para atualização cirúrgica
        RelatorioTerrestre persistente = repository.findById(id).orElse(null);
        
        if (persistente == null) {
            // NOVO RELATÓRIO
            relatorio.setId(id);
            relatorio.setDataInicio(relatorio.getDespacho().getDataInicio());
            if (relatorio.getDataFim() == null) {
                relatorio.setDataFim(LocalDateTime.now());
            }
            
            // Vincula coleções
            if (relatorio.getPropriedades() != null) {
                relatorio.getPropriedades().forEach(p -> p.setRelatorio(relatorio));
            }
            if (relatorio.getAnexos() != null) {
                relatorio.getAnexos().forEach(a -> a.setRelatorio(relatorio));
            }
            
            persistente = repository.save(relatorio);
        } else {
            // EDIÇÃO / MERGE MANUAL
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
            persistente.setKmFinal(relatorio.getKmFinal());
            persistente.setDataFim(LocalDateTime.now());

            // Sincroniza Propriedades (Mantendo IDs existentes onde possível)
            updatePropriedades(persistente, relatorio.getPropriedades());

            // Sincroniza Anexos
            updateAnexos(persistente, relatorio.getAnexos());
            
            persistente = repository.save(persistente);
        }
        
        // Atualiza o Despacho vinculado
        var despacho = persistente.getDespacho();
        despacho.setStatus(SituacaoDespacho.CONCLUIDO);
        despacho.setDataFim(persistente.getDataFim());
        // despachoService.salvar(despacho); // Save implícito pelo @Transactional

        // Inicializa todas as coleções antes de retornar (evita LazyInitializationException na serialização JSON)
        inicializarColecoes(persistente);
        
        return persistente;
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
