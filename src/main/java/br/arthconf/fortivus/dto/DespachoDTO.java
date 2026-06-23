package br.arthconf.fortivus.dto;

import br.arthconf.fortivus.domain.CategoriaOperacao;
import br.arthconf.fortivus.domain.SituacaoDespacho;

import java.time.LocalDateTime;
import java.util.UUID;

public record DespachoDTO(
    Long id,
    Long ordemServicoId,
    UUID escalaId,
    UUID responsavelId,
    CategoriaOperacao categoria,
    String descricaoTarefa,
    SituacaoDespacho status,
    LocalDateTime dataInicio,
    LocalDateTime dataFim,
    Double latitude,
    Double longitude
) {
    public String getCodigoVisual() {
        return id != null ? "D" + id : null;
    }
}
