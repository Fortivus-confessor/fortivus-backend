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
import br.arthconf.fortivus.application.port.in.BuscarRelatorioMaquinarioUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioMaquinarioUseCase;
import br.arthconf.fortivus.application.port.in.BuscarRelatorioTerrestreUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioTerrestreUseCase;
import br.arthconf.fortivus.domain.model.Despacho;
import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.infrastructure.persistence.entity.PropriedadeRelatorioEntity;
import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioTerrestreEntity;
import br.arthconf.fortivus.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
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
    private final AtualizarStatusDespachoUseCase atualizarStatusDespachoUseCase;
    private final ListarDespachosUseCase listarDespachosUseCase;
    private final BuscarDespachoPorIdUseCase buscarDespachoPorIdUseCase;
    private final DeletarDespachoUseCase deletarDespachoUseCase;
    private final DespachoRepository despachoRepository;
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
        return ResponseEntity.ok(null);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestParam SituacaoDespacho status) {
        atualizarStatusDespachoUseCase.executar(id, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
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
        log.info("Recebendo relatório terrestre para DespachoEntity ID: {}", dto.despachoId());

        DespachoEntity despachoEntity = despachoRepository.findByIdFetched(dto.despachoId()).orElse(null);

        RelatorioTerrestreEntity relatorio = buscarRelatorioTerrestreUseCase.executar(dto.despachoId())
                .orElseGet(() -> {
                    RelatorioTerrestreEntity novo = new RelatorioTerrestreEntity();
                    novo.setDespacho(despachoEntity);
                    return novo;
                });
        relatorio.setAcoesRealizadas(dto.acoesRealizadas());
        relatorio.setOrgaosApoio(dto.orgaosApoio());
        relatorio.setOutrosOrgaosDescricao(dto.outrosOrgaosDescricao());
        relatorio.setHouveUsoAgua(dto.houveUsoAgua());
        relatorio.setVolumeAguaLitros(dto.volumeAguaLitros());
        relatorio.setOrigensAgua(dto.origensAgua());
        relatorio.setOutraOrigemAguaDescricao(dto.outraOrigemAguaDescricao());
        relatorio.setHouveApoioPropriedades(dto.houveApoioPropriedades());
        relatorio.setHouveRecusaPropriedades(dto.houveRecusaPropriedades());
        relatorio.setPossivelOrigemIncendio(dto.possivelOrigemIncendio());
        relatorio.setEfetividadeCombate(dto.efetividadeCombate());
        relatorio.setNecessidadeReforco(dto.necessidadeReforco());
        relatorio.setTiposReforcoNecessarios(dto.tiposReforcoNecessarios());
        relatorio.setHistoricoDescritivo(dto.historicoDescritivo());
        relatorio.setResultadoOcorrencia(dto.resultadoOcorrencia());
        relatorio.setOutroResultadoDescricao(dto.outroResultadoDescricao());

        if (dto.areaAtuacaoLat() != null && dto.areaAtuacaoLng() != null) {
            GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
            relatorio.setAreaAtuacaoGeom(gf.createPoint(new Coordinate(dto.areaAtuacaoLng(), dto.areaAtuacaoLat())));
        }

        if (dto.propriedades() != null) {
            List<PropriedadeRelatorioEntity> propriedades = new ArrayList<>();
            for (var p : dto.propriedades()) {
                var prop = new PropriedadeRelatorioEntity();
                prop.setNomePropriedade(p.nomePropriedade());
                prop.setResponsavel(p.responsavel());
                prop.setTelefone(p.telefone());
                prop.setTipoRegistro(p.tipoRegistro());
                prop.setTipoApoio(p.tipoApoio());
                prop.setQuantidadeApoio(p.quantidadeApoio());
                prop.setDescricaoApoioOutro(p.descricaoApoioOutro());
                prop.setMotivoRecusa(p.motivoRecusa());
                prop.setDescricaoRecusaOutro(p.descricaoRecusaOutro());
                if (p.localizacaoLat() != null && p.localizacaoLng() != null) {
                    GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
                    prop.setLocalizacaoGeom(gf.createPoint(new Coordinate(p.localizacaoLng(), p.localizacaoLat())));
                }
                prop.setRelatorio(relatorio);
                propriedades.add(prop);
            }
            relatorio.setPropriedades(propriedades);
        }

        RelatorioTerrestreEntity salvo = salvarRelatorioTerrestreUseCase.executar(relatorio);
        log.info("Relatório terrestre salvo com sucesso para DespachoEntity ID: {}", dto.despachoId());
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
        log.info("Recebendo relatório aéreo para DespachoEntity ID: {}", dto.despachoId());
        return ResponseEntity.ok(salvarRelatorioAereoUseCase.salvar(dto.despachoId(), dto));
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
        log.info("Recebendo relatório maquinário para DespachoEntity ID: {}", dto.despachoId());
        return ResponseEntity.ok(salvarRelatorioMaquinarioUseCase.salvar(dto.despachoId(), dto));
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

    private RelatorioTerrestreDTO toRelatorioDTO(RelatorioTerrestreEntity r) {
        Double areaLat = null, areaLng = null;
        if (r.getAreaAtuacaoGeom() != null) {
            areaLat = r.getAreaAtuacaoGeom().getCoordinate().y;
            areaLng = r.getAreaAtuacaoGeom().getCoordinate().x;
        }
        List<RelatorioTerrestreDTO.PropriedadeRelatorioDTO> props = new ArrayList<>();
        if (r.getPropriedades() != null) {
            for (var p : r.getPropriedades()) {
                Double pLat = null, pLng = null;
                if (p.getLocalizacaoGeom() != null) {
                    pLat = p.getLocalizacaoGeom().getCoordinate().y;
                    pLng = p.getLocalizacaoGeom().getCoordinate().x;
                }
                props.add(new RelatorioTerrestreDTO.PropriedadeRelatorioDTO(
                        p.getId(), p.getNomePropriedade(), p.getResponsavel(), p.getTelefone(),
                        pLat, pLng, p.getTipoRegistro(), p.getTipoApoio(), p.getQuantidadeApoio(),
                        p.getDescricaoApoioOutro(), p.getMotivoRecusa(), p.getDescricaoRecusaOutro()
                ));
            }
        }
        return new RelatorioTerrestreDTO(
                r.getDespacho().getId(),
                r.getAcoesRealizadas(),
                r.getOrgaosApoio(),
                r.getOutrosOrgaosDescricao(),
                areaLat, areaLng,
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
