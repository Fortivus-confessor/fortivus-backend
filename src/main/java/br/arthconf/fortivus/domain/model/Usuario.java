package br.arthconf.fortivus.domain.model;

import br.arthconf.fortivus.domain.CentroComando;
import br.arthconf.fortivus.domain.EstadoOperacionalUsuario;
import br.arthconf.fortivus.domain.PerfilAcesso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private UUID id;
    private String nome;
    private String primeiroNome;
    private String email;
    private String cpf;
    private String rg;
    private String matricula;
    private String posto;
    private LocalDate dataNascimento;
    private String tipoSanguineo;
    private String fotoUrl;
    private String senha;
    private PerfilAcesso perfil;

    @Builder.Default
    private EstadoOperacionalUsuario estadoOperacional = EstadoOperacionalUsuario.DISPONIVEL;

    private CentroComando centroComando;
    private Equipe equipe;
}
