package br.arthconf.fortivus.dto;

import br.arthconf.fortivus.domain.model.CategoriaOperacao;

import java.util.UUID;

public record EquipeDTO(
    UUID id,
    String nome,
    CategoriaOperacao categoria,
    UUID centroComandoId
) {}
