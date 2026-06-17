package br.arthconf.fortivus.dto;

import br.arthconf.fortivus.domain.EstadoEquipamento;

import java.util.UUID;

public record EquipamentoDTO(
    UUID id,
    String nome,
    String identificador,
    EstadoEquipamento estado,
    UUID equipeId
) {}
