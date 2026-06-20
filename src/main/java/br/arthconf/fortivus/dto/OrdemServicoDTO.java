package br.arthconf.fortivus.dto;

import br.arthconf.fortivus.domain.SituacaoOrdemServico;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrdemServicoDTO(
    Long id,
    String localizacaoTexto,
    String descricaoTarefa,
    UUID escalaId,
    UUID relatorId,
    LocalDateTime dataCriacao,
    SituacaoOrdemServico status,
    String eventoFogoId,
    Double latitude,
    Double longitude,
    String tipoDespacho,
    UUID comandoId
) {}
