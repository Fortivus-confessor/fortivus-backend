package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.application.port.in.BuscarCentroComandoPorIdUseCase;
import br.arthconf.fortivus.application.port.in.DeletarCentroComandoUseCase;
import br.arthconf.fortivus.application.port.in.ListarCentrosComandoUseCase;
import br.arthconf.fortivus.application.port.in.SalvarCentroComandoUseCase;
import br.arthconf.fortivus.domain.model.CentroComando;
import br.arthconf.fortivus.dto.CentroComandoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    private final ListarCentrosComandoUseCase listarUseCase;
    private final BuscarCentroComandoPorIdUseCase buscarUseCase;
    private final SalvarCentroComandoUseCase salvarUseCase;
    private final DeletarCentroComandoUseCase deletarUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<List<CentroComandoDTO>> listar() {
        List<CentroComandoDTO> lista = listarUseCase.listarTodos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<Page<CentroComandoDTO>> listarPaginado(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(listarUseCase.listarPaginado(pageable).map(this::toDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<CentroComandoDTO> buscarPorId(@PathVariable UUID id) {
        return buscarUseCase.executar(id)
                .map(c -> ResponseEntity.ok(toDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<CentroComandoDTO> salvar(@RequestBody CentroComandoDTO dto) {
        CentroComando centro = fromDTO(dto);
        CentroComando salvo = salvarUseCase.executar(centro);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(toDTO(salvo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<CentroComandoDTO> atualizar(@PathVariable UUID id, @RequestBody CentroComandoDTO dto) {
        CentroComando centro = buscarUseCase.executar(id)
                .orElseThrow(() -> new RuntimeException("Centro de Comando não encontrado"));
        centro.setNome(dto.nome());
        centro.setEndereco(dto.endereco());
        centro.setTelefone(dto.telefone());
        centro.setCentral(dto.central());
        centro.setLatitude(dto.latitude());
        centro.setLongitude(dto.longitude());
        return ResponseEntity.ok(toDTO(salvarUseCase.executar(centro)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        deletarUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

    private CentroComandoDTO toDTO(CentroComando c) {
        return new CentroComandoDTO(c.getId(), c.getNome(), c.getEndereco(), c.getTelefone(),
                c.isCentral(), c.getLatitude(), c.getLongitude());
    }

    private CentroComando fromDTO(CentroComandoDTO dto) {
        return CentroComando.builder()
                .id(dto.id())
                .nome(dto.nome())
                .endereco(dto.endereco())
                .telefone(dto.telefone())
                .central(dto.central())
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .build();
    }
}
