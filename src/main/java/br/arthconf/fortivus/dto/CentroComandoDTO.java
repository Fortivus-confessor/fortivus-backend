package br.arthconf.fortivus.dto;

import java.util.UUID;

public record CentroComandoDTO(
    UUID id,
    String nome,
    String endereco,
    String telefone,
    boolean central,
    Double latitude,
    Double longitude
) {}
