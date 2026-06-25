package br.arthconf.fortivus.domain.model;

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
    private CentroComando centroComando;
    
    private List<Usuario> usuarios;
}
