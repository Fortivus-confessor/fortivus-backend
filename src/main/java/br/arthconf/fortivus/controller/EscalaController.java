package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.application.port.in.*;
import br.arthconf.fortivus.domain.model.Equipe;
import br.arthconf.fortivus.domain.model.Escala;
import br.arthconf.fortivus.domain.model.Usuario;
import br.arthconf.fortivus.dto.EscalaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/operacional/escalas")
@RequiredArgsConstructor
public class EscalaController {

    private final CriarEscalaUseCase criarEscalaUseCase;
    private final ListarEscalasUseCase listarEscalasUseCase;
    private final BuscarEscalaPorIdUseCase buscarEscalaUseCase;
    private final EncerrarEscalaUseCase encerrarEscalaUseCase;
    private final DeletarEscalaUseCase deletarEscalaUseCase;
    private final ObterUsuarioLogadoUseCase obterUsuarioLogadoUseCase;
    private final ListarUsuariosUseCase listarUsuariosUseCase;
    private final GerenciarEquipeUseCase gerenciarEquipeUseCase;
    private final ListarEquipesUseCase listarEquipesUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<EscalaDTO>> listar() {
        Usuario logado = obterUsuarioLogadoUseCase.getUsuarioLogado();
        List<Escala> escalas;
        if (logado != null && "ROLE_CENTRO_COMANDO".equals(logado.getPerfil().name())
                && logado.getCentroComando() != null) {
            escalas = listarEscalasUseCase.listarPorCentroComando(logado.getCentroComando().getId());
        } else {
            escalas = listarEscalasUseCase.listarTodas();
        }
        return ResponseEntity.ok(escalas.stream().map(this::toDTO).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    @Transactional(readOnly = true)
    public ResponseEntity<EscalaDTO> buscarPorId(@PathVariable UUID id) {
        Escala escala = buscarEscalaUseCase.executar(id)
                .orElseThrow(() -> new RuntimeException("Escala não encontrada: " + id));
        return ResponseEntity.ok(toDTO(escala));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<EscalaDTO> salvar(@RequestBody EscalaDTO dto) {
        Usuario logado = obterUsuarioLogadoUseCase.getUsuarioLogado();

        Equipe equipe = gerenciarEquipeUseCase.buscarPorId(dto.equipeId());
        if (logado != null && "ROLE_CENTRO_COMANDO".equals(logado.getPerfil().name())) {
            if (equipe.getCentroComando() == null
                    || !equipe.getCentroComando().getId().equals(logado.getCentroComando().getId())) {
                throw new AccessDeniedException("Equipe não pertence ao seu Centro de Comando");
            }
        }

        CriarEscalaUseCase.Command command = new CriarEscalaUseCase.Command(
                dto.id(),
                dto.equipeId(),
                dto.veiculoId(),
                dto.comandanteId(),
                dto.dataInicio() != null ? dto.dataInicio() : LocalDateTime.now(),
                dto.dataFim(),
                dto.integranteIds()
        );

        Escala criada = criarEscalaUseCase.executar(command);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(uri).body(toDTO(criada));
    }

    @PatchMapping("/{id}/encerrar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<Void> encerrar(@PathVariable UUID id) {
        encerrarEscalaUseCase.executar(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        deletarEscalaUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/centro/{id}/ativos")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<CentroAtivosDTO> listarAtivosCentro(@PathVariable UUID id) {
        var equipes = listarEquipesUseCase.buscarPorCentro(id).stream()
                .map(e -> new EquipeSimplesDTO(e.getId(), e.getNome()))
                .toList();

        var usuarios = listarUsuariosUseCase.buscarPorCentro(id).stream()
                .map(u -> new UsuarioSimplesDTO(
                        u.getId(),
                        u.getNome(),
                        u.getEstadoOperacional().getDescricao(),
                        "DISPONIVEL".equals(u.getEstadoOperacional().name())
                ))
                .toList();

        return ResponseEntity.ok(new CentroAtivosDTO(equipes, usuarios));
    }

    @GetMapping("/centro/{id}/ativas")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<List<EscalaSimplesDTO>> listarEscalasAtivasCentro(@PathVariable UUID id) {
        var lista = listarEscalasUseCase.listarAtivasPorCentro(id).stream()
                .map(e -> new EscalaSimplesDTO(e.getId(), e.getEquipeNome(), e.getComandanteNome()))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/paged")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<Page<EscalaDTO>> listarPaginado(@PageableDefault(size = 10) Pageable pageable) {
        Usuario logado = obterUsuarioLogadoUseCase.getUsuarioLogado();
        UUID centroId = null;
        if (logado != null && "ROLE_CENTRO_COMANDO".equals(logado.getPerfil().name())
                && logado.getCentroComando() != null) {
            centroId = logado.getCentroComando().getId();
        }
        return ResponseEntity.ok(listarEscalasUseCase.listarPaginado(centroId, pageable).map(this::toDTO));
    }

    private EscalaDTO toDTO(Escala escala) {
        return new EscalaDTO(
                escala.getId(),
                escala.getEquipeId(),
                escala.getVeiculoId(),
                escala.getComandanteId(),
                escala.getDataInicio(),
                escala.getDataFim(),
                escala.isAtiva(),
                escala.getIntegrantesIds(),
                escala.getEquipeNome(),
                escala.getComandanteNome()
        );
    }

    public record EscalaSimplesDTO(UUID id, String equipeNome, String comandanteNome) {}
    public record EquipeSimplesDTO(UUID id, String nome) {}
    public record UsuarioSimplesDTO(UUID id, String nome, String estadoDescricao, boolean disponivel) {}
    public record CentroAtivosDTO(List<EquipeSimplesDTO> equipes, List<UsuarioSimplesDTO> usuarios) {}
}
