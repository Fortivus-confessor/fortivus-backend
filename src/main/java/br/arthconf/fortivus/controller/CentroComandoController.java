package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.CentroComando;
import br.arthconf.fortivus.dto.CentroComandoDTO;
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
@RequestMapping("/api/v1/admin/centros")
@RequiredArgsConstructor
public class CentroComandoController {

    private final CentroComandoService centroService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<List<CentroComandoDTO>> listar() {
        List<CentroComandoDTO> lista = centroService.listarTodos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<CentroComandoDTO> buscarPorId(@PathVariable UUID id) {
        CentroComando centro = centroService.buscarPorId(id);
        return ResponseEntity.ok(toDTO(centro));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<CentroComandoDTO> salvar(@RequestBody CentroComandoDTO dto) {
        CentroComando centro = toEntity(dto);
        centro.updateGeom();
        CentroComando salvo = centroService.salvar(centro);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();
                
        return ResponseEntity.created(uri).body(toDTO(salvo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<CentroComandoDTO> atualizar(@PathVariable UUID id, @RequestBody CentroComandoDTO dto) {
        CentroComando centro = centroService.buscarPorId(id);
        centro.setNome(dto.nome());
        centro.setEndereco(dto.endereco());
        centro.setTelefone(dto.telefone());
        centro.setCentral(dto.central());
        centro.setLatitude(dto.latitude());
        centro.setLongitude(dto.longitude());
        centro.updateGeom();
        
        CentroComando atualizado = centroService.salvar(centro);
        return ResponseEntity.ok(toDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        centroService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private CentroComandoDTO toDTO(CentroComando centro) {
        centro.loadCoordinates();
        return new CentroComandoDTO(
                centro.getId(),
                centro.getNome(),
                centro.getEndereco(),
                centro.getTelefone(),
                centro.isCentral(),
                centro.getLatitude(),
                centro.getLongitude()
        );
    }

    private CentroComando toEntity(CentroComandoDTO dto) {
        CentroComando centro = new CentroComando();
        if (dto.id() != null) {
            centro.setId(dto.id());
        }
        centro.setNome(dto.nome());
        centro.setEndereco(dto.endereco());
        centro.setTelefone(dto.telefone());
        centro.setCentral(dto.central());
        centro.setLatitude(dto.latitude());
        centro.setLongitude(dto.longitude());
        return centro;
    }

    @org.springframework.web.bind.annotation.GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public org.springframework.http.ResponseEntity<org.springframework.data.domain.Page<CentroComandoDTO>> listarPaginado(
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
        return org.springframework.http.ResponseEntity.ok(centroService.listarPaginado(pageable).map(this::toDTO));
    }
}
