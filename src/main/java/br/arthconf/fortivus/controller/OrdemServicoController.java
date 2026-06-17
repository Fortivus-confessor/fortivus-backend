package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.OrdemServico;
import br.arthconf.fortivus.dto.OrdemServicoDTO;
import br.arthconf.fortivus.service.OrdemServicoService;
import br.arthconf.fortivus.service.EscalaService;
import br.arthconf.fortivus.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/operacional/os")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService osService;
    private final EscalaService escalaService;
    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<List<OrdemServicoDTO>> listar() {
        List<OrdemServicoDTO> ordens = osService.listarTodas().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ordens);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<OrdemServicoDTO> buscarPorId(@PathVariable Long id) {
        OrdemServico os = osService.buscarPorId(id);
        return ResponseEntity.ok(toDTO(os));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<OrdemServicoDTO> salvar(@RequestBody OrdemServicoDTO dto) {
        OrdemServico os = toEntity(dto);
        OrdemServico salva = osService.criarOS(os);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salva.getId())
                .toUri();
                
        return ResponseEntity.created(uri).body(toDTO(salva));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        osService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private OrdemServicoDTO toDTO(OrdemServico os) {
        return new OrdemServicoDTO(
                os.getId(),
                os.getLocalizacaoTexto(),
                os.getDescricaoTarefa(),
                os.getEscala() != null ? os.getEscala().getId() : null,
                os.getRelator() != null ? os.getRelator().getId() : null,
                os.getDataCriacao(),
                os.getStatus()
        );
    }

    private OrdemServico toEntity(OrdemServicoDTO dto) {
        OrdemServico os = new OrdemServico();
        if (dto.id() != null) {
            os.setId(dto.id());
        }
        os.setLocalizacaoTexto(dto.localizacaoTexto());
        os.setDescricaoTarefa(dto.descricaoTarefa());
        os.setStatus(dto.status());
        
        // Aqui assumimos que existam metodos buscarPorId em escalaService e usuarioService
        // Se os metodos tiverem nomes diferentes, sera necessario ajuste futuro.
        if (dto.escalaId() != null) {
            os.setEscala(escalaService.buscarPorId(dto.escalaId()));
        }
        if (dto.relatorId() != null) {
            os.setRelator(usuarioService.buscarPorId(dto.relatorId()));
        }
        return os;
    }
}
