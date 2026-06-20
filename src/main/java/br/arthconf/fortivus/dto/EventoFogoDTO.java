package br.arthconf.fortivus.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventoFogoDTO(
    UUID id,
    String codigo,
    String descricao,
    Double latitude,
    Double longitude,
    LocalDateTime dataCriacao,
    String status
) {}
