package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.Despacho;
import br.arthconf.fortivus.domain.OrdemServico;
import br.arthconf.fortivus.dto.CadastrarOsDespachoDTO;
import br.arthconf.fortivus.dto.OrdemServicoDTO;
import br.arthconf.fortivus.service.OrdemServicoService;
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

    private final OrdemServicoService ordemServicoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<List<OrdemServicoDTO>> listar() {
        List<OrdemServicoDTO> list = ordemServicoService.listarTodas().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<OrdemServicoDTO> criar(@RequestBody CadastrarOsDespachoDTO dto) {
        OrdemServico os = ordemServicoService.cadastrarOsEDespacho(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(os.getId())
                .toUri();
        return ResponseEntity.created(uri).body(toDTO(os));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<OrdemServicoDTO> editar(@PathVariable Long id, @RequestBody CadastrarOsDespachoDTO dto) {
        OrdemServico os = ordemServicoService.editarOrdemServico(id, dto);
        return ResponseEntity.ok(toDTO(os));
    }

    private OrdemServicoDTO toDTO(OrdemServico os) {
        Despacho primeiroDespacho = os.getDespachos().isEmpty() ? null : os.getDespachos().get(0);
        String focoId = os.getFocoIncendio() != null ? os.getFocoIncendio().getId().toString() : null;
        
        Double lat = null;
        Double lng = null;
        if (primeiroDespacho != null && primeiroDespacho.getLocalizacaoGeom() instanceof org.locationtech.jts.geom.Point p) {
            lat = p.getY();
            lng = p.getX();
        }
        
        return new OrdemServicoDTO(
            os.getId(),
            os.getLocalizacaoTexto(),
            os.getDescricaoTarefa(),
            os.getEscala() != null ? os.getEscala().getId() : null,
            os.getRelator() != null ? os.getRelator().getId() : null,
            os.getDataCriacao(),
            os.getStatus(),
            focoId,
            lat,
            lng,
            primeiroDespacho != null && primeiroDespacho.getCategoria() != null ? primeiroDespacho.getCategoria().name() : null,
            os.getEscala() != null && os.getEscala().getEquipe() != null ? os.getEscala().getEquipe().getCentroComando().getId() : null
        );
    }
}
