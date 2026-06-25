package br.arthconf.fortivus.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Escala {
    private UUID id;
    private UUID equipeId;
    private String equipeNome;
    private UUID centroComandoId;
    private UUID veiculoId;
    private UUID comandanteId;
    private String comandanteNome;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private boolean ativa;
    @Builder.Default
    private List<UUID> integrantesIds = new ArrayList<>();
}
