package br.arthconf.fortivus.adapters.in.web;

import br.arthconf.fortivus.application.port.in.ListarDespachosUseCase;
import br.arthconf.fortivus.application.port.in.ObterUsuarioLogadoUseCase;
import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.domain.model.Despacho;
import br.arthconf.fortivus.domain.model.Usuario;
import br.arthconf.fortivus.dto.DespachoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints exclusivos do app mobile — não compartilhados com o web.
 * Filtra despachos pelo campo responsavel_id (FK direta no despacho),
 * diferente da listagem web que usa membros da escala.
 */
@RestController
@RequestMapping("/api/v1/mobile/despachos")
@RequiredArgsConstructor
public class MobileDespachoController {

    private final ListarDespachosUseCase listarDespachosUseCase;
    private final ObterUsuarioLogadoUseCase obterUsuarioLogadoUseCase;

    /**
     * GET /api/v1/mobile/despachos/meus
     *
     * @param situacao  ABERTA (EM_ANDAMENTO + PENDENTE_RELATORIO) | ENCERRADA (CONCLUIDO) | ausente = todos
     * @param page      número da página (0-indexed)
     * @param size      itens por página (padrão: 20)
     * @param sort      asc | desc (padrão: desc por id)
     */
    @GetMapping("/meus")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<Page<DespachoDTO>> meus(
            @RequestParam(required = false) String situacao,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "desc") String sort) {

        Usuario logado = obterUsuarioLogadoUseCase.getUsuarioLogado();
        if (logado == null) return ResponseEntity.status(401).build();

        List<SituacaoDespacho> statuses = resolverStatuses(situacao);
        Pageable pageable = PageRequest.of(page, size,
                "asc".equalsIgnoreCase(sort) ? Sort.by("id").ascending() : Sort.by("id").descending());

        Page<DespachoDTO> resultado = listarDespachosUseCase
                .listarMeusPaginado(logado.getId(), statuses, pageable)
                .map(this::toDTO);

        return ResponseEntity.ok(resultado);
    }

    private List<SituacaoDespacho> resolverStatuses(String situacao) {
        if (situacao == null || situacao.isBlank()) return List.of();
        return switch (situacao.toUpperCase()) {
            case "ABERTA"    -> List.of(SituacaoDespacho.EM_ANDAMENTO, SituacaoDespacho.PENDENTE_RELATORIO);
            case "ENCERRADA" -> List.of(SituacaoDespacho.CONCLUIDO);
            default          -> List.of();
        };
    }

    private DespachoDTO toDTO(Despacho d) {
        return new DespachoDTO(
                d.getId(),
                d.getOrdemServicoId(),
                d.getEscalaId(),
                d.getResponsavelId(),
                d.getCategoria(),
                d.getDescricaoTarefa(),
                d.getStatus(),
                d.getDataInicio(),
                d.getDataFim(),
                d.getLatitude(),
                d.getLongitude()
        );
    }
}
