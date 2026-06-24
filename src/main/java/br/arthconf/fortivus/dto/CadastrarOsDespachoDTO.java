package br.arthconf.fortivus.dto;

import br.arthconf.fortivus.domain.model.CategoriaOperacao;
import br.arthconf.fortivus.domain.SituacaoOrdemServico;

import java.util.UUID;

public record CadastrarOsDespachoDTO(
    String descricaoTarefa,
    Long eventoFogoId,
    SituacaoOrdemServico status,
    String prioridade,
    UUID escalaId,
    UUID responsavelId,
    CategoriaOperacao tipoDespacho,
    Double latitude,
    Double longitude
) {}
