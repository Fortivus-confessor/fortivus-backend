package br.arthconf.fortivus.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EscalaDTO(
    UUID id,
    UUID equipeId,
    UUID veiculoId,
    UUID comandanteId,
    LocalDateTime dataInicio,
    LocalDateTime dataFim,
    boolean ativa,
    List<UUID> integranteIds,
    String equipeNome,
    String comandanteNome
) {}
