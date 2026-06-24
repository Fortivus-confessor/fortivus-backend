package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.domain.PropriedadeRelatorio;
import br.arthconf.fortivus.domain.RelatorioTerrestre;
import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.dto.DespachoDTO;
import br.arthconf.fortivus.dto.RelatorioAereoDTO;
import br.arthconf.fortivus.dto.RelatorioMaquinarioDTO;
import br.arthconf.fortivus.dto.RelatorioTerrestreDTO;
import br.arthconf.fortivus.application.port.in.BuscarRelatorioAereoUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioAereoUseCase;
import br.arthconf.fortivus.application.port.in.BuscarRelatorioMaquinarioUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioMaquinarioUseCase;
import br.arthconf.fortivus.application.usecase.*;
import br.arthconf.fortivus.domain.model.Despacho;
import br.arthconf.fortivus.service.OrdemServicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
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
    private final br.arthconf.fortivus.repository.DespachoRepository despachoRepository;
    private final br.arthconf.fortivus.service.RelatorioTerrestreService relatorioTerrestreService;
    private final BuscarRelatorioAereoUseCase buscarRelatorioAereoUseCase;
    private final SalvarRelatorioAereoUseCase salvarRelatorioAereoUseCase;
    private final BuscarRelatorioMaquinarioUseCase buscarRelatorioMaquinarioUseCase;
    private final SalvarRelatorioMaquinarioUseCase salvarRelatorioMaquinarioUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<List<DespachoDTO>> listar() {
        return ResponseEntity.ok(listarDespachosUseCase.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<DespachoDTO> buscarPorId(@PathVariable Long id) {
        return buscarDespachoPorIdUseCase.executar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<DespachoDTO> salvar(@RequestBody DespachoDTO dto) {
        Despacho despacho = br.arthconf.fortivus.domain.model.Despacho.builder()
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
                .map(d -> ResponseEntity.created(location).body(d))
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

    /**
     * GET /despachos/{id}/relatorio-terrestre
     * Busca o relatório terrestre existente para um DespachoEntity.
     * Retorna 404 se o DespachoEntity não possui relatório ainda.
     */
    @GetMapping("/{id}/relatorio-terrestre")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<RelatorioTerrestreDTO> buscarRelatorioTerrestre(@PathVariable Long id) {
        RelatorioTerrestre relatorio = relatorioTerrestreService.buscarPorDespachoId(id);
        if (relatorio == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toRelatorioDTO(relatorio));
    }

    /**
     * POST /despachos/finalizar-terrestre
     * Cria ou atualiza o relatório terrestre de um DespachoEntity via JSON.
     */
    @PostMapping("/finalizar-terrestre")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<RelatorioTerrestreDTO> finalizarTerrestre(@RequestBody RelatorioTerrestreDTO dto) {
        log.info("Recebendo relatório terrestre para DespachoEntity ID: {}", dto.despachoId());

        var DespachoEntity = despachoRepository.findByIdFetched(dto.despachoId()).orElse(null);

        RelatorioTerrestre relatorio = relatorioTerrestreService.buscarPorDespachoId(dto.despachoId());
        if (relatorio == null) {
            relatorio = new RelatorioTerrestre();
            relatorio.setDespacho(DespachoEntity);
        }
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

        // Geolocalização da área de atuação
        if (dto.areaAtuacaoLat() != null && dto.areaAtuacaoLng() != null) {
            GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
            relatorio.setAreaAtuacaoGeom(gf.createPoint(new Coordinate(dto.areaAtuacaoLng(), dto.areaAtuacaoLat())));
        }

        // Propriedades rurais
        if (dto.propriedades() != null) {
            List<PropriedadeRelatorio> propriedades = new ArrayList<>();
            for (var p : dto.propriedades()) {
                var prop = new PropriedadeRelatorio();
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

        RelatorioTerrestre salvo = relatorioTerrestreService.salvar(relatorio);
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
        RelatorioAereoDTO salvo = salvarRelatorioAereoUseCase.salvar(dto.despachoId(), dto);
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
        log.info("Recebendo relatório maquinário para DespachoEntity ID: {}", dto.despachoId());
        RelatorioMaquinarioDTO salvo = salvarRelatorioMaquinarioUseCase.salvar(dto.despachoId(), dto);
        return ResponseEntity.ok(salvo);
    }

    private DespachoDTO toDTO(DespachoEntity DespachoEntity) {
        Double lat = DespachoEntity.getLatitude();
        Double lng = DespachoEntity.getLongitude();
        return new DespachoDTO(
                DespachoEntity.getId(),
                DespachoEntity.getOrdemServico() != null ? DespachoEntity.getOrdemServico().getId() : null,
                DespachoEntity.getEscala() != null ? DespachoEntity.getEscala().getId() : null,
                DespachoEntity.getResponsavel() != null ? DespachoEntity.getResponsavel().getId() : null,
                DespachoEntity.getCategoria(),
                DespachoEntity.getDescricaoTarefa(),
                DespachoEntity.getStatus(),
                DespachoEntity.getDataInicio(),
                DespachoEntity.getDataFim(),
                lat,
                lng
        );
    }

    private RelatorioTerrestreDTO toRelatorioDTO(RelatorioTerrestre r) {
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



    @GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<org.springframework.data.domain.Page<DespachoDTO>> listarPaginado(
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(listarDespachosUseCase.listarPaginado(pageable));
    }
}

