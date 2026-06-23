package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.Usuario;
import br.arthconf.fortivus.dto.UsuarioDTO;
import br.arthconf.fortivus.service.UsuarioService;
import br.arthconf.fortivus.service.CentroComandoService;
import br.arthconf.fortivus.service.EquipeService;
import br.arthconf.fortivus.service.FileStorageService;
import br.arthconf.fortivus.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final CentroComandoService centroService;
    private final EquipeService equipeService;
    private final FileStorageService storageService;
    private final KeycloakService keycloakService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable UUID id) {
        Usuario usuario = usuarioService.buscarPorId(id);
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
            usuarioParaSalvar = usuarioService.buscarPorId(usuario.getId());
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
            usuarioParaSalvar.setCentroComando(centroService.buscarPorId(centroComandoId));
        } else {
            usuarioParaSalvar.setCentroComando(null);
        }
        
        if (equipeId != null) {
            usuarioParaSalvar.setEquipe(equipeService.buscarPorId(equipeId));
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
        
        usuarioService.salvar(usuarioParaSalvar);

        if (isNovoUsuario) {
            keycloakService.criarUsuario(usuarioParaSalvar.getEmail(), usuario.getSenha(), usuarioParaSalvar.getNome(), usuarioParaSalvar.getPerfil().name());
        } else {
            keycloakService.atualizarUsuario(emailAntigo, usuarioParaSalvar.getEmail(), usuarioParaSalvar.getNome(), usuarioParaSalvar.getPerfil().name());
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        Usuario u = usuarioService.buscarPorId(id);
        if (u.getFotoUrl() != null && u.getFotoUrl().startsWith("http")) {
            storageService.delete(u.getFotoUrl());
        }
        keycloakService.deletarUsuario(u.getEmail());
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/foto")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirFoto(@PathVariable UUID id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario.getFotoUrl() != null && usuario.getFotoUrl().startsWith("http")) {
            storageService.delete(usuario.getFotoUrl());
            usuario.setFotoUrl(null);
            usuarioService.salvar(usuario);
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
                null // senha vazia no retorno
        );
    }

    @org.springframework.web.bind.annotation.GetMapping("/paged")
    public org.springframework.http.ResponseEntity<org.springframework.data.domain.Page<UsuarioDTO>> listarPaginado(
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
        return org.springframework.http.ResponseEntity.ok(usuarioService.listarPaginado(pageable).map(this::toDTO));
    }
}
