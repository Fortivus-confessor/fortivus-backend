package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.dto.DespachoDTO;
import br.arthconf.fortivus.dto.RelatorioAereoDTO;
import br.arthconf.fortivus.dto.RelatorioMaquinarioDTO;
import br.arthconf.fortivus.dto.RelatorioTerrestreDTO;
import br.arthconf.fortivus.application.port.in.AtualizarStatusDespachoUseCase;
import br.arthconf.fortivus.application.port.in.BuscarDespachoPorIdUseCase;
import br.arthconf.fortivus.application.port.in.CriarDespachoUseCase;
import br.arthconf.fortivus.application.port.in.DeletarDespachoUseCase;
import br.arthconf.fortivus.application.port.in.ListarDespachosUseCase;
import br.arthconf.fortivus.application.port.in.BuscarRelatorioAereoUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioAereoUseCase;
import br.arthconf.fortivus.application.port.in.AtualizarDespachoUseCase;
import br.arthconf.fortivus.application.port.in.BuscarRelatorioMaquinarioUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioMaquinarioUseCase;
import br.arthconf.fortivus.application.port.in.BuscarRelatorioTerrestreUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioTerrestreUseCase;
import br.arthconf.fortivus.domain.model.Despacho;
import br.arthconf.fortivus.domain.model.PropriedadeRelatorio;
import br.arthconf.fortivus.domain.model.RelatorioTerrestre;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/operacional/despachos")
@RequiredArgsConstructor
@Slf4j
public class DespachoController {

    private final CriarDespachoUseCase criarDespachoUseCase;
    private final AtualizarDespachoUseCase atualizarDespachoUseCase;
    private final AtualizarStatusDespachoUseCase atualizarStatusDespachoUseCase;
    private final ListarDespachosUseCase listarDespachosUseCase;
    private final BuscarDespachoPorIdUseCase buscarDespachoPorIdUseCase;
    private final DeletarDespachoUseCase deletarDespachoUseCase;
    private final BuscarRelatorioTerrestreUseCase buscarRelatorioTerrestreUseCase;
    private final SalvarRelatorioTerrestreUseCase salvarRelatorioTerrestreUseCase;
    private final BuscarRelatorioAereoUseCase buscarRelatorioAereoUseCase;
    private final SalvarRelatorioAereoUseCase salvarRelatorioAereoUseCase;
    private final BuscarRelatorioMaquinarioUseCase buscarRelatorioMaquinarioUseCase;
    private final SalvarRelatorioMaquinarioUseCase salvarRelatorioMaquinarioUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<List<DespachoDTO>> listar() {
        List<DespachoDTO> list = listarDespachosUseCase.listarTodos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<Page<DespachoDTO>> listarPaginado(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(listarDespachosUseCase.listarPaginado(pageable).map(this::toDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<DespachoDTO> buscarPorId(@PathVariable Long id) {
        return buscarDespachoPorIdUseCase.executar(id)
                .map(d -> ResponseEntity.ok(toDTO(d)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<DespachoDTO> salvar(@RequestBody DespachoDTO dto) {
        Despacho despacho = Despacho.builder()
                .ordemServicoId(dto.ordemServicoId())
                .escalaId(dto.escalaId())
                .responsavelId(dto.responsavelId())
                .categoria(dto.categoria())
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .descricaoTarefa(dto.descricaoTarefa())
                .status(SituacaoDespacho.EM_ANDAMENTO)
                .build();

        Despacho salvo = criarDespachoUseCase.executar(despacho);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();
        return buscarDespachoPorIdUseCase.executar(salvo.getId())
                .map(d -> ResponseEntity.created(location).body(toDTO(d)))
                .orElse(ResponseEntity.internalServerError().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<DespachoDTO> atualizar(@PathVariable Long id, @RequestBody DespachoDTO dto) {
        Despacho despacho = Despacho.builder()
                .ordemServicoId(dto.ordemServicoId())
                .escalaId(dto.escalaId())
                .responsavelId(dto.responsavelId())
                .categoria(dto.categoria())
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .descricaoTarefa(dto.descricaoTarefa())
                .status(dto.status())
                .dataInicio(dto.dataInicio())
                .dataFim(dto.dataFim())
                .build();

        Despacho salvo = atualizarDespachoUseCase.executar(id, despacho);
        return ResponseEntity.ok(toDTO(salvo));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestParam SituacaoDespacho status) {
        atualizarStatusDespachoUseCase.executar(id, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        deletarDespachoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/relatorio-terrestre")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<RelatorioTerrestreDTO> buscarRelatorioTerrestre(@PathVariable Long id) {
        return buscarRelatorioTerrestreUseCase.executar(id)
                .map(this::toRelatorioDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/finalizar-terrestre")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<RelatorioTerrestreDTO> finalizarTerrestre(@RequestBody RelatorioTerrestreDTO dto) {
        log.info("Recebendo relatório terrestre para Despacho ID: {}", dto.despachoId());

        List<PropriedadeRelatorio> propriedades = null;
        if (dto.propriedades() != null) {
            propriedades = new ArrayList<>();
            for (var p : dto.propriedades()) {
                propriedades.add(PropriedadeRelatorio.builder()
                        .id(p.id())
                        .nomePropriedade(p.nomePropriedade())
                        .responsavel(p.responsavel())
                        .telefone(p.telefone())
                        .localizacaoLat(p.localizacaoLat())
                        .localizacaoLng(p.localizacaoLng())
                        .tipoRegistro(p.tipoRegistro())
                        .tipoApoio(p.tipoApoio())
                        .quantidadeApoio(p.quantidadeApoio())
                        .descricaoApoioOutro(p.descricaoApoioOutro())
                        .motivoRecusa(p.motivoRecusa())
                        .descricaoRecusaOutro(p.descricaoRecusaOutro())
                        .build());
            }
        }

        RelatorioTerrestre domain = RelatorioTerrestre.builder()
                .despachoId(dto.despachoId())
                .acoesRealizadas(dto.acoesRealizadas())
                .orgaosApoio(dto.orgaosApoio())
                .outrosOrgaosDescricao(dto.outrosOrgaosDescricao())
                .areaAtuacaoLat(dto.areaAtuacaoLat())
                .areaAtuacaoLng(dto.areaAtuacaoLng())
                .houveUsoAgua(dto.houveUsoAgua())
                .volumeAguaLitros(dto.volumeAguaLitros())
                .origensAgua(dto.origensAgua())
                .outraOrigemAguaDescricao(dto.outraOrigemAguaDescricao())
                .houveApoioPropriedades(dto.houveApoioPropriedades())
                .houveRecusaPropriedades(dto.houveRecusaPropriedades())
                .possivelOrigemIncendio(dto.possivelOrigemIncendio())
                .efetividadeCombate(dto.efetividadeCombate())
                .necessidadeReforco(dto.necessidadeReforco())
                .tiposReforcoNecessarios(dto.tiposReforcoNecessarios())
                .historicoDescritivo(dto.historicoDescritivo())
                .resultadoOcorrencia(dto.resultadoOcorrencia())
                .outroResultadoDescricao(dto.outroResultadoDescricao())
                .propriedades(propriedades)
                .dataInicio(dto.dataInicio())
                .dataFim(dto.dataFim())
                .build();

        RelatorioTerrestre salvo = salvarRelatorioTerrestreUseCase.executar(domain);
        log.info("Relatório terrestre salvo com sucesso para Despacho ID: {}", dto.despachoId());
        return ResponseEntity.ok(toRelatorioDTO(salvo));
    }

    @GetMapping("/{id}/relatorio-aereo")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<RelatorioAereoDTO> buscarRelatorioAereo(@PathVariable Long id) {
        return buscarRelatorioAereoUseCase.buscarPorDespachoId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/finalizar-aereo")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<RelatorioAereoDTO> finalizarAereo(@RequestBody RelatorioAereoDTO dto) {
        log.info("Recebendo relatório aéreo para Despacho ID: {}", dto.despachoId());
        RelatorioAereoDTO salvo = salvarRelatorioAereoUseCase.salvar(dto.despachoId(), dto);
        atualizarStatusDespachoUseCase.executar(dto.despachoId(), SituacaoDespacho.CONCLUIDO);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("/{id}/relatorio-maquinario")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<RelatorioMaquinarioDTO> buscarRelatorioMaquinario(@PathVariable Long id) {
        return buscarRelatorioMaquinarioUseCase.buscarPorDespachoId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/finalizar-maquinario")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<RelatorioMaquinarioDTO> finalizarMaquinario(@RequestBody RelatorioMaquinarioDTO dto) {
        log.info("Recebendo relatório maquinário para Despacho ID: {}", dto.despachoId());
        RelatorioMaquinarioDTO salvo = salvarRelatorioMaquinarioUseCase.salvar(dto.despachoId(), dto);
        atualizarStatusDespachoUseCase.executar(dto.despachoId(), SituacaoDespacho.CONCLUIDO);
        return ResponseEntity.ok(salvo);
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

    private RelatorioTerrestreDTO toRelatorioDTO(RelatorioTerrestre r) {
        List<RelatorioTerrestreDTO.PropriedadeRelatorioDTO> props = new ArrayList<>();
        if (r.getPropriedades() != null) {
            for (var p : r.getPropriedades()) {
                props.add(new RelatorioTerrestreDTO.PropriedadeRelatorioDTO(
                        p.getId(), p.getNomePropriedade(), p.getResponsavel(), p.getTelefone(),
                        p.getLocalizacaoLat(), p.getLocalizacaoLng(),
                        p.getTipoRegistro(), p.getTipoApoio(), p.getQuantidadeApoio(),
                        p.getDescricaoApoioOutro(), p.getMotivoRecusa(), p.getDescricaoRecusaOutro()
                ));
            }
        }
        return new RelatorioTerrestreDTO(
                r.getDespachoId(),
                r.getAcoesRealizadas(),
                r.getOrgaosApoio(),
                r.getOutrosOrgaosDescricao(),
                r.getAreaAtuacaoLat(),
                r.getAreaAtuacaoLng(),
                r.getHouveUsoAgua(),
                r.getVolumeAguaLitros(),
                r.getOrigensAgua(),
                r.getOutraOrigemAguaDescricao(),
                r.getHouveApoioPropriedades(),
                r.getHouveRecusaPropriedades(),
                props,
                r.getPossivelOrigemIncendio(),
                r.getOutroResultadoDescricao(),
                r.getEfetividadeCombate(),
                r.getNecessidadeReforco(),
                r.getTiposReforcoNecessarios(),
                r.getHistoricoDescritivo(),
                r.getResultadoOcorrencia(),
                r.getOutroResultadoDescricao(),
                r.getDataInicio(),
                r.getDataFim()
        );
    }
}
