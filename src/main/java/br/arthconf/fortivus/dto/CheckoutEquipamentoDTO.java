package br.arthconf.fortivus.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CheckoutEquipamentoDTO(
    UUID id,
    UUID escalaId,
    UUID equipamentoId,
    UUID responsavelEntregaId,
    LocalDateTime dataEmprestimo,
    LocalDateTime dataDevolucao,
    UUID responsavelRecebimentoId
) {}
