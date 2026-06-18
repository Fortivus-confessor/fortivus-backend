package br.arthconf.fortivus.service;

import br.arthconf.fortivus.config.RabbitMQConfig;
import br.arthconf.fortivus.dto.EventoSeveroMessage;
import br.arthconf.fortivus.domain.FocoIncendio;
import br.arthconf.fortivus.repository.FocoIncendioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FireEventListener {

    private final FocoIncendioRepository focoIncendioRepository;

    @RabbitListener(queues = RabbitMQConfig.SEVERE_EVENT_QUEUE)
    @Transactional
    public void processarAlertaSevero(EventoSeveroMessage message) {
        log.info("Recebido alerta de evento de fogo severo vindo do RabbitMQ: {}", message.getEventoId());

        String eventoRef = message.getEventoId().toString();

        if (focoIncendioRepository.existsByCodigoInpe(eventoRef)) {
            log.info("Evento de fogo {} já está registrado no centro de comando. Atualizando métricas...", eventoRef);
            FocoIncendio foco = focoIncendioRepository.findByCodigoInpe(eventoRef).orElseThrow();
            foco.setFrp(message.getFrpTotal());
            foco.setDataHoraDeteccao(message.getDataDeteccao());
            focoIncendioRepository.save(foco);
            return;
        }

        log.info("Registrando NOVO Foco de Incêndio Severo oriundo da análise espacial da NASA/FIRMS...");
        FocoIncendio foco = new FocoIncendio();
        // Vincula o ID do Evento Clusterizado ao 'codigoInpe' para mantermos o rastro da origem
        foco.setCodigoInpe(eventoRef); 
        foco.setLatitude(message.getLatitudeCentroide());
        foco.setLongitude(message.getLongitudeCentroide());
        foco.setFrp(message.getFrpTotal());
        foco.setDataHoraDeteccao(message.getDataDeteccao());
        foco.setOrigemRegistro("NASA_FIRMS");
        foco.setStatus("ATIVO");
        foco.setRiscoFogo(message.getSeveridade());

        focoIncendioRepository.save(foco);
        
        log.info("Evento severo {} salvo com sucesso na tabela tb_focos_incendio do Fortivus V2 para posterior despacho de Ordem de Serviço.", eventoRef);
    }
}
