package br.arthconf.fortivus.dto;

import br.arthconf.fortivus.domain.EstadoOperacionalUsuario;
import br.arthconf.fortivus.domain.PerfilAcesso;

import java.time.LocalDate;
import java.util.UUID;

public record UsuarioDTO(
    UUID id,
    String nome,
    String primeiroNome,
    String email,
    String cpf,
    String rg,
    String matricula,
    String posto,
    LocalDate dataNascimento,
    String tipoSanguineo,
    String fotoUrl,
    PerfilAcesso perfil,
    EstadoOperacionalUsuario estadoOperacional,
    UUID centroComandoId,
    UUID equipeId,
    String senha
) {}
