package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.Equipe;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipeDTO> salvar(@RequestBody EquipeDTO dto) {
        Equipe equipe = new Equipe();
        if (dto.id() != null) {
            equipe.setId(dto.id());
        }
        equipe.setNome(dto.nome());
        equipe.setCategoria(dto.categoria());
        equipe.setCentroComando(centroService.buscarPorId(dto.centroComandoId()));
        
        Equipe salvo = equipeService.salvar(equipe);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();
                
        return ResponseEntity.created(uri).body(toDTO(salvo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipeDTO> atualizar(@PathVariable UUID id, @RequestBody EquipeDTO dto) {
        Equipe equipe = equipeService.buscarPorId(id);
        equipe.setNome(dto.nome());
        equipe.setCategoria(dto.categoria());
        if (dto.centroComandoId() != null) {
            equipe.setCentroComando(centroService.buscarPorId(dto.centroComandoId()));
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
}
