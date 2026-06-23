package br.arthconf.fortivus.dto;

import br.arthconf.fortivus.domain.CategoriaOperacao;

import java.util.UUID;

public record VeiculoDTO(
    UUID id,
    String identificador,
    String prefixo,
    String modelo,
    CategoriaOperacao categoria,
    Integer kmAtual,
    String fotoUrl,
    UUID equipeId,
    UUID centroComandoId,
    String contrato
) {}
