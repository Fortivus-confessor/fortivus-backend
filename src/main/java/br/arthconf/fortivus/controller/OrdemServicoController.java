package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.application.port.in.*;
import br.arthconf.fortivus.domain.model.OrdemServico;
import br.arthconf.fortivus.dto.CadastrarOsDespachoDTO;
import br.arthconf.fortivus.dto.OrdemServicoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
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

    private final CriarOrdemServicoUseCase criarOsUseCase;
    private final EditarOrdemServicoUseCase editarOsUseCase;
    private final ListarOrdensServicoUseCase listarOsUseCase;
    private final BuscarOrdemServicoPorIdUseCase buscarOsUseCase;
    private final ExcluirOrdemServicoUseCase excluirOsUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<List<OrdemServicoDTO>> listar() {
        List<OrdemServicoDTO> list = listarOsUseCase.listarTodas().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<Page<OrdemServicoDTO>> listarPaginado(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(listarOsUseCase.listarPaginado(pageable).map(this::toDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<OrdemServicoDTO> buscarPorId(@PathVariable Long id) {
        return buscarOsUseCase.executar(id)
                .map(os -> ResponseEntity.ok(toDTO(os)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<OrdemServicoDTO> criar(@RequestBody CadastrarOsDespachoDTO dto) {
        OrdemServico os = criarOsUseCase.executar(new CriarOrdemServicoUseCase.Command(
                dto.descricaoTarefa(),
                dto.eventoFogoId(),
                dto.escalaId(),
                dto.responsavelId(),
                dto.tipoDespacho(),
                dto.latitude(),
                dto.longitude()
        ));
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(os.getId()).toUri();
        return ResponseEntity.created(uri).body(toDTO(os));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<OrdemServicoDTO> editar(@PathVariable Long id, @RequestBody CadastrarOsDespachoDTO dto) {
        OrdemServico os = editarOsUseCase.executar(new EditarOrdemServicoUseCase.Command(
                id, dto.descricaoTarefa(), dto.eventoFogoId()
        ));
        return ResponseEntity.ok(toDTO(os));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        excluirOsUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

    private OrdemServicoDTO toDTO(OrdemServico os) {
        return new OrdemServicoDTO(
                os.getId(),
                os.getDescricaoTarefa(),
                os.getEscalaId(),
                os.getRelatorId(),
                os.getDataCriacao(),
                os.getStatus(),
                os.getEventoFogoId() != null ? os.getEventoFogoId().toString() : null,
                os.getTipoDespacho(),
                os.getCentroComandoId(),
                os.getDataFim()
        );
    }
}
