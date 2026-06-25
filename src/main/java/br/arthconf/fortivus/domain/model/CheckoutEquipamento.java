package br.arthconf.fortivus.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutEquipamento {
    private UUID id;
    private UUID escalaId;
    private UUID equipamentoId;
    private UUID responsavelEntregaId;
    private LocalDateTime dataEmprestimo;
    private LocalDateTime dataDevolucao;
    private UUID responsavelRecebimentoId;
}
