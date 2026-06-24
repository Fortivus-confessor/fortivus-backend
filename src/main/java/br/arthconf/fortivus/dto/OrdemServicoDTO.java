package br.arthconf.fortivus.dto;

import br.arthconf.fortivus.domain.SituacaoOrdemServico;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrdemServicoDTO(
    Long id,
    String descricaoTarefa,
    UUID escalaId,
    UUID relatorId,
    LocalDateTime dataCriacao,
    SituacaoOrdemServico status,
    String eventoFogoId,
    String tipoDespacho,
    UUID comandoId,
    LocalDateTime dataFim
) {
    @com.fasterxml.jackson.annotation.JsonProperty("smartId")
    public String getSmartId() {
        return id != null ? "OS" + id : null;
    }
}
