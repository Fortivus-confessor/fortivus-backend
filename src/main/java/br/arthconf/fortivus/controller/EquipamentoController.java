package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.application.port.in.BuscarEquipamentoPorIdUseCase;
import br.arthconf.fortivus.application.port.in.DeletarEquipamentoUseCase;
import br.arthconf.fortivus.application.port.in.ListarEquipamentosUseCase;
import br.arthconf.fortivus.application.port.in.SalvarEquipamentoUseCase;
import br.arthconf.fortivus.domain.model.Equipamento;
import br.arthconf.fortivus.dto.EquipamentoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ativos/almoxarifado")
@RequiredArgsConstructor
public class EquipamentoController {

    private final ListarEquipamentosUseCase listarUseCase;
    private final BuscarEquipamentoPorIdUseCase buscarUseCase;
    private final SalvarEquipamentoUseCase salvarUseCase;
    private final DeletarEquipamentoUseCase deletarUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<List<EquipamentoDTO>> listar() {
        return ResponseEntity.ok(listarUseCase.executar().stream()
                .map(this::toDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<EquipamentoDTO> buscarPorId(@PathVariable UUID id) {
        return buscarUseCase.executar(id)
                .map(e -> ResponseEntity.ok(toDTO(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<EquipamentoDTO> salvar(@RequestBody EquipamentoDTO dto) {
        Equipamento salvo = salvarUseCase.executar(new SalvarEquipamentoUseCase.Command(
                dto.id(), dto.nome(), dto.identificador(), dto.estado(), dto.equipeId()));
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(toDTO(salvo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        deletarUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

    private EquipamentoDTO toDTO(Equipamento e) {
        return new EquipamentoDTO(e.getId(), e.getNome(), e.getIdentificador(), e.getEstado(), e.getEquipeId());
    }
}
