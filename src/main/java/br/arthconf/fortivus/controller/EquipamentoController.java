package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.Equipamento;
import br.arthconf.fortivus.dto.EquipamentoDTO;
import br.arthconf.fortivus.service.EquipamentoService;
import br.arthconf.fortivus.service.EquipeService;
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

    private final EquipamentoService equipamentoService;
    private final EquipeService equipeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<List<EquipamentoDTO>> listar() {
        List<EquipamentoDTO> equipamentos = equipamentoService.listarTodos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(equipamentos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<EquipamentoDTO> buscarPorId(@PathVariable UUID id) {
        Equipamento equipamento = equipamentoService.buscarPorId(id);
        return ResponseEntity.ok(toDTO(equipamento));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<EquipamentoDTO> salvar(@RequestBody EquipamentoDTO dto) {
        Equipamento equipamento = new Equipamento();
        if (dto.id() != null) {
            equipamento = equipamentoService.buscarPorId(dto.id());
        }
        
        equipamento.setNome(dto.nome());
        equipamento.setIdentificador(dto.identificador());
        if (dto.estado() != null) {
            equipamento.setEstado(dto.estado());
        }
        if (dto.equipeId() != null) {
            equipamento.setEquipe(br.arthconf.fortivus.infrastructure.persistence.mapper.EquipeMapper.toEntity(equipeService.buscarPorId(dto.equipeId())));
        } else {
            equipamento.setEquipe(null);
        }
        
        Equipamento salvo = equipamentoService.salvar(equipamento);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();
                
        return ResponseEntity.created(uri).body(toDTO(salvo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        equipamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private EquipamentoDTO toDTO(Equipamento equipamento) {
        return new EquipamentoDTO(
                equipamento.getId(),
                equipamento.getNome(),
                equipamento.getIdentificador(),
                equipamento.getEstado(),
                equipamento.getEquipe() != null ? equipamento.getEquipe().getId() : null
        );
    }
}
