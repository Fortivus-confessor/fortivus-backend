package br.arthconf.fortivus.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventoSeveroMessage {
    private Long eventoId;
    private Double latitudeCentroide;
    private Double longitudeCentroide;
    private Double frpTotal;
    private Integer totalFocos;
    private LocalDateTime dataDeteccao;
    private String severidade;
}
