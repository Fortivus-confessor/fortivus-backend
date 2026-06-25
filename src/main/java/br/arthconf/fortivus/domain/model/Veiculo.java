package br.arthconf.fortivus.domain.model;

import br.arthconf.fortivus.domain.model.Equipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Veiculo {
    private UUID id;
    private String identificador;
    private String prefixo;
    private String modelo;
    private CategoriaOperacao categoria;
    private Integer kmAtual;
    private String contrato;
    private String fotoUrl;
    private Equipe equipe;
    private CentroComando centroComando;
}
