package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.model.Equipe;
import br.arthconf.fortivus.dto.EquipeDTO;
import br.arthconf.fortivus.service.EquipeService;
import br.arthconf.fortivus.service.CentroComandoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/equipes")
@RequiredArgsConstructor
public class EquipeController {

    private final EquipeService equipeService;
    private final CentroComandoService centroService;
    private final br.arthconf.fortivus.service.UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<List<EquipeDTO>> listar() {
        List<EquipeDTO> lista = equipeService.listarTodas().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<EquipeDTO> buscarPorId(@PathVariable UUID id) {
        Equipe equipe = equipeService.buscarPorId(id);
        return ResponseEntity.ok(toDTO(equipe));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<EquipeDTO> salvar(@RequestBody EquipeDTO dto) {
        Equipe equipe = new Equipe();
        if (dto.id() != null) {
            equipe.setId(dto.id());
        }
        equipe.setNome(dto.nome());
        equipe.setCategoria(dto.categoria());
        
        br.arthconf.fortivus.domain.model.Usuario logado = usuarioService.getUsuarioLogado();
        if (logado != null && logado.getPerfil() == br.arthconf.fortivus.domain.PerfilAcesso.ROLE_CENTRO_COMANDO && logado.getCentroComando() != null) {
            equipe.setCentroComando(logado.getCentroComando());
        } else {
            equipe.setCentroComando(centroService.buscarPorId(dto.centroComandoId()));
        }
        
        Equipe salvo = equipeService.salvar(equipe);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();
                
        return ResponseEntity.created(uri).body(toDTO(salvo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<EquipeDTO> atualizar(@PathVariable UUID id, @RequestBody EquipeDTO dto) {
        Equipe equipe = equipeService.buscarPorId(id);
        
        br.arthconf.fortivus.domain.model.Usuario logado = usuarioService.getUsuarioLogado();
        if (logado != null && logado.getPerfil() == br.arthconf.fortivus.domain.PerfilAcesso.ROLE_CENTRO_COMANDO && logado.getCentroComando() != null) {
            if (equipe.getCentroComando() == null || !equipe.getCentroComando().getId().equals(logado.getCentroComando().getId())) {
                return ResponseEntity.status(403).build();
            }
        }
        
        equipe.setNome(dto.nome());
        equipe.setCategoria(dto.categoria());
        
        if (logado == null || logado.getPerfil() != br.arthconf.fortivus.domain.PerfilAcesso.ROLE_CENTRO_COMANDO) {
            if (dto.centroComandoId() != null) {
                equipe.setCentroComando(centroService.buscarPorId(dto.centroComandoId()));
            }
        }
        
        Equipe atualizado = equipeService.salvar(equipe);
        return ResponseEntity.ok(toDTO(atualizado));
    }

    private EquipeDTO toDTO(Equipe equipe) {
        return new EquipeDTO(
                equipe.getId(),
                equipe.getNome(),
                equipe.getCategoria(),
                equipe.getCentroComando() != null ? equipe.getCentroComando().getId() : null
        );
    }

    @org.springframework.web.bind.annotation.GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public org.springframework.http.ResponseEntity<org.springframework.data.domain.Page<EquipeDTO>> listarPaginado(
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
        return org.springframework.http.ResponseEntity.ok(equipeService.listarPaginado(pageable).map(this::toDTO));
    }
}
