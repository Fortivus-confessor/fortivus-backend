package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.application.port.in.BuscarCentroComandoPorIdUseCase;
import br.arthconf.fortivus.application.port.in.GerenciarEquipeUseCase;
import br.arthconf.fortivus.application.port.in.GerenciarUsuarioUseCase;
import br.arthconf.fortivus.application.port.in.ListarUsuariosUseCase;
import br.arthconf.fortivus.application.port.in.ObterUsuarioLogadoUseCase;
import br.arthconf.fortivus.domain.model.Usuario;
import br.arthconf.fortivus.dto.UsuarioDTO;
import br.arthconf.fortivus.service.FileStorageService;
import br.arthconf.fortivus.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final ListarUsuariosUseCase listarUsuariosUseCase;
    private final GerenciarUsuarioUseCase gerenciarUsuarioUseCase;
    private final ObterUsuarioLogadoUseCase obterUsuarioLogadoUseCase;
    private final GerenciarEquipeUseCase gerenciarEquipeUseCase;
    private final BuscarCentroComandoPorIdUseCase buscarCentroUseCase;
    private final FileStorageService storageService;
    private final KeycloakService keycloakService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> usuarios = listarUsuariosUseCase.listarTodos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<Page<UsuarioDTO>> listarPaginado(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(listarUsuariosUseCase.listarPaginado(pageable).map(this::toDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable UUID id) {
        Usuario usuario = gerenciarUsuarioUseCase.buscarPorId(id);
        return ResponseEntity.ok(toDTO(usuario));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> salvar(
            @ModelAttribute Usuario usuario,
            @RequestParam(value = "centroComandoId", required = false) UUID centroComandoId,
            @RequestParam(value = "equipeId", required = false) UUID equipeId,
            @RequestParam(value = "fotoArquivo", required = false) MultipartFile fotoArquivo) throws IOException {

        Usuario usuarioParaSalvar;
        boolean isNovoUsuario = (usuario.getId() == null);
        String emailAntigo = null;

        if (!isNovoUsuario) {
            usuarioParaSalvar = gerenciarUsuarioUseCase.buscarPorId(usuario.getId());
            emailAntigo = usuarioParaSalvar.getEmail();
            usuarioParaSalvar.setNome(usuario.getNome());
            usuarioParaSalvar.setPrimeiroNome(usuario.getPrimeiroNome());
            usuarioParaSalvar.setEmail(usuario.getEmail());
            usuarioParaSalvar.setCpf(usuario.getCpf());
            usuarioParaSalvar.setRg(usuario.getRg());
            usuarioParaSalvar.setMatricula(usuario.getMatricula());
            usuarioParaSalvar.setDataNascimento(usuario.getDataNascimento());
            usuarioParaSalvar.setTipoSanguineo(usuario.getTipoSanguineo());
            usuarioParaSalvar.setPosto(usuario.getPosto());
            usuarioParaSalvar.setPerfil(usuario.getPerfil());
            usuarioParaSalvar.setEstadoOperacional(usuario.getEstadoOperacional());
        } else {
            usuarioParaSalvar = usuario;
        }

        if (centroComandoId != null) {
            usuarioParaSalvar.setCentroComando(buscarCentroUseCase.executar(centroComandoId)
                    .orElseThrow(() -> new RuntimeException("Centro de Comando não encontrado")));
        } else {
            usuarioParaSalvar.setCentroComando(null);
        }

        if (equipeId != null) {
            usuarioParaSalvar.setEquipe(gerenciarEquipeUseCase.buscarPorId(equipeId));
        } else {
            usuarioParaSalvar.setEquipe(null);
        }

        if (fotoArquivo != null && !fotoArquivo.isEmpty()) {
            if (usuarioParaSalvar.getFotoUrl() != null && usuarioParaSalvar.getFotoUrl().startsWith("http")) {
                storageService.delete(usuarioParaSalvar.getFotoUrl());
            }
            String url = storageService.upload(fotoArquivo, "usuarios");
            usuarioParaSalvar.setFotoUrl(url);
        }

        if (isNovoUsuario) {
            keycloakService.criarUsuario(usuarioParaSalvar.getEmail(), usuario.getSenha(), usuarioParaSalvar.getNome(), usuarioParaSalvar.getPerfil().name());
        } else {
            keycloakService.atualizarUsuario(emailAntigo, usuarioParaSalvar.getEmail(), usuarioParaSalvar.getNome(), usuarioParaSalvar.getPerfil().name());
        }

        gerenciarUsuarioUseCase.salvar(usuarioParaSalvar);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        Usuario u = gerenciarUsuarioUseCase.buscarPorId(id);
        if (u.getFotoUrl() != null && u.getFotoUrl().startsWith("http")) {
            storageService.delete(u.getFotoUrl());
        }
        keycloakService.deletarUsuario(u.getEmail());
        gerenciarUsuarioUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/foto")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirFoto(@PathVariable UUID id) {
        Usuario usuario = gerenciarUsuarioUseCase.buscarPorId(id);
        if (usuario.getFotoUrl() != null && usuario.getFotoUrl().startsWith("http")) {
            storageService.delete(usuario.getFotoUrl());
            usuario.setFotoUrl(null);
            gerenciarUsuarioUseCase.salvar(usuario);
        }
        return ResponseEntity.noContent().build();
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getPrimeiroNome(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getRg(),
                usuario.getMatricula(),
                usuario.getPosto(),
                usuario.getDataNascimento(),
                usuario.getTipoSanguineo(),
                usuario.getFotoUrl(),
                usuario.getPerfil(),
                usuario.getEstadoOperacional(),
                usuario.getCentroComando() != null ? usuario.getCentroComando().getId() : null,
                usuario.getEquipe() != null ? usuario.getEquipe().getId() : null,
                null
        );
    }
}
