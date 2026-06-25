package br.arthconf.fortivus.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CentroComando {
    private UUID id;
    private String nome;
    private String endereco;
    private String telefone;
    private boolean central;
    private Double latitude;
    private Double longitude;
}
