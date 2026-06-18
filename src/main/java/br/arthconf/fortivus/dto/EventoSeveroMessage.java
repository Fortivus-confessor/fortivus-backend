package br.arthconf.fortivus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoSeveroMessage {
    private UUID eventoId;
    private Double latitudeCentroide;
    private Double longitudeCentroide;
    private Double frpTotal;
    private Integer totalFocos;
    private LocalDateTime dataDeteccao;
    private String severidade;
}
