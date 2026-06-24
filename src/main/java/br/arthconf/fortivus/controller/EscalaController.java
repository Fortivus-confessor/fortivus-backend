package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.Escala;
import br.arthconf.fortivus.dto.EscalaDTO;
import br.arthconf.fortivus.service.EscalaService;
import br.arthconf.fortivus.service.EquipeService;
import br.arthconf.fortivus.service.VeiculoService;
import br.arthconf.fortivus.service.UsuarioService;
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
@RequestMapping("/api/v1/operacional/escalas")
@RequiredArgsConstructor
public class EscalaController {

    private final EscalaService escalaService;
    private final EquipeService equipeService;
    private final VeiculoService veiculoService;
    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<List<EscalaDTO>> listar() {
        List<EscalaDTO> escalas = escalaService.listarTodas().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(escalas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<EscalaDTO> buscarPorId(@PathVariable UUID id) {
        Escala escala = escalaService.buscarPorId(id);
        return ResponseEntity.ok(toDTO(escala));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<EscalaDTO> salvar(@RequestBody EscalaDTO dto) {
        br.arthconf.fortivus.domain.model.Usuario logado = usuarioService.getUsuarioLogado();
        
        var equipe = equipeService.buscarPorId(dto.equipeId());
        if (logado != null && "ROLE_CENTRO_COMANDO".equals(logado.getPerfil().name())) {
            if (equipe.getCentroComando() == null || !equipe.getCentroComando().getId().equals(logado.getCentroComando().getId())) {
                throw new org.springframework.security.access.AccessDeniedException("Equipe não pertence ao seu Centro de Comando");
            }
        }
        
        Escala escala = new Escala();
        if (dto.id() != null) {
            escala = escalaService.buscarPorId(dto.id());
        }
        
        escala.setEquipe(br.arthconf.fortivus.infrastructure.persistence.mapper.EquipeMapper.toEntity(equipe));
        if (dto.veiculoId() != null) {
            var veiculo = veiculoService.buscarPorId(dto.veiculoId());
            if (logado != null && "ROLE_CENTRO_COMANDO".equals(logado.getPerfil().name())) {
                if (veiculo.getCentroComando() == null || !veiculo.getCentroComando().getId().equals(logado.getCentroComando().getId())) {
                    throw new org.springframework.security.access.AccessDeniedException("Veículo não pertence ao seu Centro de Comando");
                }
            }
            escala.setVeiculo(br.arthconf.fortivus.infrastructure.persistence.mapper.VeiculoMapper.toEntity(veiculo));
        }
        escala.setComandante(br.arthconf.fortivus.infrastructure.persistence.mapper.UsuarioMapper.toEntity(usuarioService.buscarPorId(dto.comandanteId())));
        escala.setDataInicio(dto.dataInicio() != null ? dto.dataInicio() : java.time.LocalDateTime.now());
        escala.setDataFim(dto.dataFim());
        
        escalaService.ativarEscala(escala, dto.integranteIds());
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(escala.getId())
                .toUri();
                
        return ResponseEntity.created(uri).body(toDTO(escala));
    }

    @PatchMapping("/{id}/encerrar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<Void> encerrar(@PathVariable UUID id) {
        escalaService.encerrarEscala(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        escalaService.deletarEscala(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/centro/{id}/ativos")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<CentroAtivosDTO> listarAtivosCentro(@PathVariable UUID id) {
        var equipes = equipeService.buscarPorCentro(id).stream()
                .map(e -> new EquipeSimplesDTO(e.getId(), e.getNome()))
                .toList();
        
        var usuarios = usuarioService.buscarPorCentro(id).stream()
                .map(u -> new UsuarioSimplesDTO(
                        u.getId(), 
                        u.getNome(), 
                        u.getEstadoOperacional().getDescricao(),
                        u.getEstadoOperacional().name().equals("DISPONIVEL")
                ))
                .toList();

        return ResponseEntity.ok(new CentroAtivosDTO(equipes, usuarios));
    }

    @GetMapping("/centro/{id}/ativas")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<List<EscalaSimplesDTO>> listarEscalasAtivasCentro(@PathVariable UUID id) {
        var listas = escalaService.listarAtivas().stream()
                .filter(e -> e.getEquipe().getCentroComando().getId().equals(id))
                .map(e -> new EscalaSimplesDTO(e.getId(), e.getEquipe().getNome(), e.getComandante().getNome()))
                .toList();
        return ResponseEntity.ok(listas);
    }

    private EscalaDTO toDTO(Escala escala) {
        List<UUID> integrantes = escala.getIntegrantes().stream()
                .map(br.arthconf.fortivus.infrastructure.persistence.entity.UsuarioEntity::getId)
                .collect(Collectors.toList());
                
        return new EscalaDTO(
                escala.getId(),
                escala.getEquipe() != null ? escala.getEquipe().getId() : null,
                escala.getVeiculo() != null ? escala.getVeiculo().getId() : null,
                escala.getComandante() != null ? escala.getComandante().getId() : null,
                escala.getDataInicio(),
                escala.getDataFim(),
                escala.isAtiva(),
                integrantes,
                escala.getEquipe() != null ? escala.getEquipe().getNome() : null,
                escala.getComandante() != null ? escala.getComandante().getNome() : null
        );
    }

    public record EscalaSimplesDTO(UUID id, String equipeNome, String comandanteNome) {}
    public record EquipeSimplesDTO(UUID id, String nome) {}
    public record UsuarioSimplesDTO(UUID id, String nome, String estadoDescricao, boolean disponivel) {}
    public record CentroAtivosDTO(List<EquipeSimplesDTO> equipes, List<UsuarioSimplesDTO> usuarios) {}

    @org.springframework.web.bind.annotation.GetMapping("/paged")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public org.springframework.http.ResponseEntity<org.springframework.data.domain.Page<EscalaDTO>> listarPaginado(
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
        return org.springframework.http.ResponseEntity.ok(escalaService.listarPaginado(pageable).map(this::toDTO));
    }
}
