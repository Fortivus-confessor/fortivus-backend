package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.VerificarOrdensServicoUseCase;
import br.arthconf.fortivus.application.port.out.OrdemServicoRepositoryPort;
import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.domain.model.Despacho;
import br.arthconf.fortivus.domain.model.OrdemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificarOrdensServicoService implements VerificarOrdensServicoUseCase {

    private final OrdemServicoRepositoryPort osPort;

    @Override
    @Transactional
    public void executar() {
        List<OrdemServico> ordens = osPort.listarEmExecucaoComDespachos();
        LocalDateTime limite = LocalDateTime.now().minusDays(2);
        int concluidas = 0;

        for (OrdemServico os : ordens) {
            List<Despacho> despachos = os.getDespachos();
            if (despachos == null || despachos.isEmpty()) continue;

            boolean todosConcluidos = despachos.stream()
                    .allMatch(d -> d.getStatus() == SituacaoDespacho.CONCLUIDO);
            if (!todosConcluidos) continue;

            LocalDateTime ultimaData = despachos.stream()
                    .map(Despacho::getDataInicio)
                    .filter(d -> d != null)
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.MIN);

            if (ultimaData.isBefore(limite)) {
                os.concluir(LocalDateTime.now());
                osPort.salvar(os);
                concluidas++;
            }
        }

        if (concluidas > 0) {
            log.info("{} Ordem(ns) de Serviço marcadas como CONCLUIDA automaticamente.", concluidas);
        }
    }
}
