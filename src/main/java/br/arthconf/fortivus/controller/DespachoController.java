package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.Despacho;
import br.arthconf.fortivus.domain.PropriedadeRelatorio;
import br.arthconf.fortivus.domain.RelatorioTerrestre;
import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.dto.DespachoDTO;
import br.arthconf.fortivus.dto.RelatorioTerrestreDTO;
import br.arthconf.fortivus.service.DespachoService;
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

    private final DespachoService despachoService;
    private final OrdemServicoService osService;
    private final br.arthconf.fortivus.service.EscalaService escalaService;
    private final br.arthconf.fortivus.service.RelatorioTerrestreService relatorioTerrestreService;
    private final br.arthconf.fortivus.service.UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<List<DespachoDTO>> listar() {
        List<DespachoDTO> despachos = despachoService.listarTodos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(despachos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<DespachoDTO> buscarPorId(@PathVariable Long id) {
        Despacho despacho = despachoService.buscarPorId(id);
        return ResponseEntity.ok(toDTO(despacho));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<DespachoDTO> salvar(@RequestBody DespachoDTO dto) {
        Despacho despacho = new Despacho();
        if (dto.id() != null) {
            despacho = despachoService.buscarPorId(dto.id());
        } else {
            despacho.setStatus(SituacaoDespacho.EM_ANDAMENTO);
            // ID e data inicio serão gerados pelo DespachoService
        }

        var os = osService.buscarPorId(dto.ordemServicoId());
        despacho.setOrdemServico(os);
        var escala = escalaService.buscarPorId(dto.escalaId());
        despacho.setEscala(escala);
        despacho.setCategoria(escala.getEquipe().getCategoria());
        despacho.setDescricaoTarefa(dto.descricaoTarefa());

        if (dto.responsavelId() != null) {
            var usuario = usuarioService.buscarPorId(dto.responsavelId());
            despacho.setResponsavel(br.arthconf.fortivus.infrastructure.persistence.mapper.UsuarioMapper.toEntity(usuario));
        }

        if (dto.latitude() != null && dto.longitude() != null) {
            despacho.setLatitude(dto.latitude());
            despacho.setLongitude(dto.longitude());
        }

        Despacho salvo = despachoService.salvar(despacho);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();
                
        return ResponseEntity.created(uri).body(toDTO(salvo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<DespachoDTO> atualizar(@PathVariable Long id, @RequestBody DespachoDTO dto) {
        Despacho despacho = despachoService.buscarPorId(id);
        if (despacho == null) {
            return ResponseEntity.notFound().build();
        }

        var os = osService.buscarPorId(dto.ordemServicoId());
        despacho.setOrdemServico(os);
        var escala = escalaService.buscarPorId(dto.escalaId());
        despacho.setEscala(escala);
        despacho.setCategoria(escala.getEquipe().getCategoria());
        despacho.setDescricaoTarefa(dto.descricaoTarefa());

        if (dto.responsavelId() != null) {
            var usuario = usuarioService.buscarPorId(dto.responsavelId());
            despacho.setResponsavel(br.arthconf.fortivus.infrastructure.persistence.mapper.UsuarioMapper.toEntity(usuario));
        } else {
            despacho.setResponsavel(null);
        }

        if (dto.status() != null) despacho.setStatus(dto.status());
        if (dto.dataInicio() != null) despacho.setDataInicio(dto.dataInicio());
        despacho.setDataFim(dto.dataFim());

        if (dto.latitude() != null && dto.longitude() != null) {
            despacho.setLatitude(dto.latitude());
            despacho.setLongitude(dto.longitude());
        } else {
            despacho.setLatitude(null);
            despacho.setLongitude(null);
        }

        Despacho salvo = despachoService.salvar(despacho);
        return ResponseEntity.ok(toDTO(salvo));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestParam SituacaoDespacho status) {
        despachoService.atualizarStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        despachoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /despachos/{id}/relatorio-terrestre
     * Busca o relatório terrestre existente para um despacho.
     * Retorna 404 se o despacho não possui relatório ainda.
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
     * Cria ou atualiza o relatório terrestre de um despacho via JSON.
     */
    @PostMapping("/finalizar-terrestre")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<RelatorioTerrestreDTO> finalizarTerrestre(@RequestBody RelatorioTerrestreDTO dto) {
        log.info("Recebendo relatório terrestre para despacho ID: {}", dto.despachoId());

        var despacho = despachoService.buscarPorId(dto.despachoId());

        RelatorioTerrestre relatorio = relatorioTerrestreService.buscarPorDespachoId(dto.despachoId());
        if (relatorio == null) {
            relatorio = new RelatorioTerrestre();
            relatorio.setDespacho(despacho);
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
        log.info("Relatório terrestre salvo com sucesso para despacho ID: {}", dto.despachoId());
        return ResponseEntity.ok(toRelatorioDTO(salvo));
    }

    private DespachoDTO toDTO(Despacho despacho) {
        Double lat = despacho.getLatitude();
        Double lng = despacho.getLongitude();
        return new DespachoDTO(
                despacho.getId(),
                despacho.getOrdemServico() != null ? despacho.getOrdemServico().getId() : null,
                despacho.getEscala() != null ? despacho.getEscala().getId() : null,
                despacho.getResponsavel() != null ? despacho.getResponsavel().getId() : null,
                despacho.getCategoria(),
                despacho.getDescricaoTarefa(),
                despacho.getStatus(),
                despacho.getDataInicio(),
                despacho.getDataFim(),
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
        return ResponseEntity.ok(despachoService.listarPaginado(pageable).map(this::toDTO));
    }
}
