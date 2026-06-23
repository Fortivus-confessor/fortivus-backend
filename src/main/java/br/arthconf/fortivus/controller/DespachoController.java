package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.Despacho;
import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.dto.DespachoDTO;
import br.arthconf.fortivus.service.DespachoService;
import br.arthconf.fortivus.service.OrdemServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.MediaType;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/operacional/despachos")
@RequiredArgsConstructor
public class DespachoController {

    private final DespachoService despachoService;
    private final OrdemServicoService osService;
    private final br.arthconf.fortivus.service.EscalaService escalaService;
    private final br.arthconf.fortivus.service.RelatorioTerrestreService relatorioTerrestreService;
    private final br.arthconf.fortivus.service.FileStorageService storageService;
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
            despacho.setId(System.currentTimeMillis());
            despacho.setStatus(SituacaoDespacho.EM_ANDAMENTO);
            despacho.setDataInicio(java.time.LocalDateTime.now());
        }

        var os = osService.buscarPorId(dto.ordemServicoId());
        despacho.setOrdemServico(os);
        var escala = escalaService.buscarPorId(dto.escalaId());
        despacho.setEscala(escala);
        despacho.setCategoria(escala.getEquipe().getCategoria());
        despacho.setDescricaoTarefa(dto.descricaoTarefa());

        if (dto.responsavelId() != null) {
            var usuario = usuarioService.buscarPorId(dto.responsavelId());
            despacho.setResponsavel(usuario);
        }

        if (dto.latitude() != null && dto.longitude() != null) {
            org.locationtech.jts.geom.GeometryFactory gf = new org.locationtech.jts.geom.GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);
            despacho.setLocalizacaoGeom(gf.createPoint(new org.locationtech.jts.geom.Coordinate(dto.longitude(), dto.latitude())));
        }

        Despacho salvo = despachoService.salvar(despacho);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();
                
        return ResponseEntity.created(uri).body(toDTO(salvo));
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

    // Mantido como consumo multipart/form-data para compatibilidade com o envio de relatórios e arquivos
    @PostMapping(value = "/finalizar-terrestre", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<Void> finalizarTerrestre(
            @ModelAttribute br.arthconf.fortivus.domain.RelatorioTerrestre relatorio,
            @RequestParam("despachoId") Long despachoId,
            @RequestParam(value = "areaAtuacaoLat", required = false) Double lat,
            @RequestParam(value = "areaAtuacaoLng", required = false) Double lng,
            @RequestParam(value = "anexosParaRemover", required = false) java.util.List<java.util.UUID> anexosParaRemover,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {
            
        var despacho = despachoService.buscarPorId(despachoId);
        relatorio.setDespacho(despacho);
        
        br.arthconf.fortivus.domain.RelatorioTerrestre relatorioExistente = relatorioTerrestreService.buscarPorDespachoId(despachoId);
        java.util.List<br.arthconf.fortivus.domain.AnexoRelatorio> listaFinalAnexos = new java.util.ArrayList<>();
        
        if (relatorioExistente != null && relatorioExistente.getAnexos() != null) {
            listaFinalAnexos.addAll(relatorioExistente.getAnexos());
        }

        if (anexosParaRemover != null) {
            listaFinalAnexos.removeIf(a -> anexosParaRemover.contains(a.getId()));
        }

        if (lat != null && lng != null) {
            org.locationtech.jts.geom.GeometryFactory gf = new org.locationtech.jts.geom.GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);
            relatorio.setAreaAtuacaoGeom(gf.createPoint(new org.locationtech.jts.geom.Coordinate(lng, lat)));
        }

        String storageFolder = "relatorios/despacho-" + despachoId;
        if (files != null && files.length > 0) {
            for (var file : files) {
                if (!file.isEmpty()) {
                    String url = storageService.upload(file, storageFolder);
                    var anexo = new br.arthconf.fortivus.domain.AnexoRelatorio();
                    anexo.setNomeArquivo(file.getOriginalFilename());
                    anexo.setChaveS3(url);
                    anexo.setContentType(file.getContentType());
                    anexo.setTamanho(file.getSize());
                    anexo.setRelatorio(relatorio);
                    listaFinalAnexos.add(anexo);
                }
            }
        }
        relatorio.setAnexos(listaFinalAnexos);

        relatorioTerrestreService.salvar(relatorio);
        return ResponseEntity.ok().build();
    }

    private DespachoDTO toDTO(Despacho despacho) {
        Double lat = null;
        Double lng = null;
        if (despacho.getLocalizacaoGeom() != null) {
            lat = despacho.getLocalizacaoGeom().getCoordinate().y;
            lng = despacho.getLocalizacaoGeom().getCoordinate().x;
        }
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

    @org.springframework.web.bind.annotation.GetMapping("/paged")
    public org.springframework.http.ResponseEntity<org.springframework.data.domain.Page<DespachoDTO>> listarPaginado(
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
        return org.springframework.http.ResponseEntity.ok(despachoService.listarPaginado(pageable).map(this::toDTO));
    }
}
