package br.arthconf.fortivus.service;

import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.infrastructure.persistence.entity.OrdemServicoEntity;
import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.domain.SituacaoOrdemServico;
import br.arthconf.fortivus.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdemServicoCron {

    private final OrdemServicoRepository ordemServicoRepository;

    @Scheduled(cron = "0 0 2 * * *") // Roda às 2 da manhã todos os dias
    @Transactional
    public void verificarEConcluirOrdensServico() {
        log.info("Executando verificação de conclusão de Ordens de Serviço (Cron)...");
        
        List<OrdemServicoEntity> ordens = ordemServicoRepository.findAllFetched();
        LocalDateTime limite = LocalDateTime.now().minusDays(2);
        
        int concluidas = 0;
        
        for (OrdemServicoEntity os : ordens) {
            if (os.getStatus() != SituacaoOrdemServico.EM_EXECUCAO) {
                continue;
            }
            
            List<DespachoEntity> despachos = os.getDespachos();
            if (despachos == null || despachos.isEmpty()) {
                continue;
            }
            
            boolean todosConcluidos = true;
            LocalDateTime ultimoDespachoData = LocalDateTime.MIN;
            
            for (DespachoEntity d : despachos) {
                if (d.getStatus() != SituacaoDespacho.CONCLUIDO) {
                    todosConcluidos = false;
                    break;
                }
                LocalDateTime dataReferencia = d.getDataInicio() != null ? d.getDataInicio() : LocalDateTime.MIN;
                if (dataReferencia.isAfter(ultimoDespachoData)) {
                    ultimoDespachoData = dataReferencia;
                }
            }
            
            // Regra: Todos despachos concluídos E passaram 2 dias desde o último DespachoEntity
            if (todosConcluidos && ultimoDespachoData.isBefore(limite)) {
                os.setStatus(SituacaoOrdemServico.CONCLUIDA);
                os.setDataFim(LocalDateTime.now());
                ordemServicoRepository.save(os);
                concluidas++;
            }
        }
        
        if (concluidas > 0) {
            log.info("{} Ordem(ns) de Serviço marcadas como CONCLUIDA automaticamente.", concluidas);
        }
    }
}


