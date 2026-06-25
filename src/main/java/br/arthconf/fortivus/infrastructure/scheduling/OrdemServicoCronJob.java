package br.arthconf.fortivus.infrastructure.scheduling;

import br.arthconf.fortivus.application.port.in.VerificarOrdensServicoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrdemServicoCronJob {

    private final VerificarOrdensServicoUseCase verificarOrdensServicoUseCase;

    @Scheduled(cron = "0 0 2 * * *")
    public void verificarEConcluirOrdensServico() {
        log.info("Executando verificação de conclusão de Ordens de Serviço (Cron)...");
        verificarOrdensServicoUseCase.executar();
    }
}
