package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.application.port.in.BuscarCentroComandoPorIdUseCase;
import br.arthconf.fortivus.application.port.in.GerenciarEquipeUseCase;
import br.arthconf.fortivus.application.port.in.ListarEquipesUseCase;
import br.arthconf.fortivus.application.port.in.ObterUsuarioLogadoUseCase;
import br.arthconf.fortivus.domain.PerfilAcesso;
import br.arthconf.fortivus.domain.model.Equipe;
import br.arthconf.fortivus.dto.EquipeDTO;
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
@RequestMapping("/api/v1/admin/equipes")
@RequiredArgsConstructor
public class EquipeController {

    private final ListarEquipesUseCase listarEquipesUseCase;
    private final GerenciarEquipeUseCase gerenciarEquipeUseCase;
    private final ObterUsuarioLogadoUseCase obterUsuarioLogadoUseCase;
    private final BuscarCentroComandoPorIdUseCase buscarCentroUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<List<EquipeDTO>> listar() {
        List<EquipeDTO> lista = listarEquipesUseCase.listarTodas().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<Page<EquipeDTO>> listarPaginado(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(listarEquipesUseCase.listarPaginado(pageable).map(this::toDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<EquipeDTO> buscarPorId(@PathVariable UUID id) {
        Equipe equipe = gerenciarEquipeUseCase.buscarPorId(id);
        return ResponseEntity.ok(toDTO(equipe));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<EquipeDTO> salvar(@RequestBody EquipeDTO dto) {
        Equipe equipe = new Equipe();
        if (dto.id() != null) {
            equipe.setId(dto.id());
        }
        equipe.setNome(dto.nome());
        equipe.setCategoria(dto.categoria());

        var logado = obterUsuarioLogadoUseCase.getUsuarioLogado();
        if (logado != null && logado.getPerfil() == PerfilAcesso.ROLE_CENTRO_COMANDO && logado.getCentroComando() != null) {
            equipe.setCentroComando(logado.getCentroComando());
        } else {
            equipe.setCentroComando(buscarCentroUseCase.executar(dto.centroComandoId())
                    .orElseThrow(() -> new RuntimeException("Centro de Comando não encontrado")));
        }

        Equipe salvo = gerenciarEquipeUseCase.salvar(equipe);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(toDTO(salvo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<EquipeDTO> atualizar(@PathVariable UUID id, @RequestBody EquipeDTO dto) {
        Equipe equipe = gerenciarEquipeUseCase.buscarPorId(id);

        var logado = obterUsuarioLogadoUseCase.getUsuarioLogado();
        if (logado != null && logado.getPerfil() == PerfilAcesso.ROLE_CENTRO_COMANDO && logado.getCentroComando() != null) {
            if (equipe.getCentroComando() == null || !equipe.getCentroComando().getId().equals(logado.getCentroComando().getId())) {
                return ResponseEntity.status(403).build();
            }
        }

        equipe.setNome(dto.nome());
        equipe.setCategoria(dto.categoria());

        if (logado == null || logado.getPerfil() != PerfilAcesso.ROLE_CENTRO_COMANDO) {
            if (dto.centroComandoId() != null) {
                equipe.setCentroComando(buscarCentroUseCase.executar(dto.centroComandoId())
                        .orElseThrow(() -> new RuntimeException("Centro de Comando não encontrado")));
            }
        }

        Equipe atualizado = gerenciarEquipeUseCase.salvar(equipe);
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
