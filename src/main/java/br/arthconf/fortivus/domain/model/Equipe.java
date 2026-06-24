package br.arthconf.fortivus.domain.model;

import br.arthconf.fortivus.domain.CentroComando;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Equipe {
    private UUID id;
    private String nome;
    private CategoriaOperacao categoria;
    
    // Phase 4 will decouple CentroComando
    private CentroComando centroComando;
    
    private List<Usuario> usuarios;
}
