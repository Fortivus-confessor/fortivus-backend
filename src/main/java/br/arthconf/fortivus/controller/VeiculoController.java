package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.Veiculo;
import br.arthconf.fortivus.dto.VeiculoDTO;
import br.arthconf.fortivus.service.FileStorageService;
import br.arthconf.fortivus.service.VeiculoService;
import br.arthconf.fortivus.service.EquipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ativos/frota")
@RequiredArgsConstructor
public class VeiculoController {

    private final VeiculoService veiculoService;
    private final EquipeService equipeService;
    private final br.arthconf.fortivus.service.CentroComandoService centroService;
    private final FileStorageService storageService;



    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<List<VeiculoDTO>> listar() {
        List<VeiculoDTO> veiculos = veiculoService.listarTodos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(veiculos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<VeiculoDTO> buscarPorId(@PathVariable UUID id) {
        Veiculo veiculo = veiculoService.buscarPorId(id);
        return ResponseEntity.ok(toDTO(veiculo));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<Void> salvar(
            @RequestParam(value = "id", required = false) UUID id,
            @RequestParam(value = "identificador", required = false) String identificador,
            @RequestParam(value = "prefixo", required = false) String prefixo,
            @RequestParam(value = "modelo", required = false) String modelo,
            @RequestParam(value = "categoria", required = false) String categoriaStr,
            @RequestParam(value = "kmAtual", required = false) Integer kmAtual,
            @RequestParam(value = "equipeId", required = false) UUID equipeId,
            @RequestParam(value = "centroComandoId", required = false) UUID centroComandoId,
            @RequestParam(value = "contrato", required = false) String contrato,
            @RequestParam(value = "fotoArquivo", required = false) MultipartFile fotoArquivo) throws IOException {

        br.arthconf.fortivus.domain.CategoriaOperacao categoria = null;
        if (categoriaStr != null && !categoriaStr.isEmpty()) {
            categoria = br.arthconf.fortivus.domain.CategoriaOperacao.fromString(categoriaStr);
        }

        Veiculo veiculoParaSalvar;
        
        if (id != null) {
            veiculoParaSalvar = veiculoService.buscarPorId(id);
        } else {
            veiculoParaSalvar = new Veiculo();
        }

        if (modelo != null) veiculoParaSalvar.setModelo(modelo);
        if (prefixo != null) veiculoParaSalvar.setPrefixo(prefixo);
        if (identificador != null) veiculoParaSalvar.setIdentificador(identificador);
        if (categoria != null) veiculoParaSalvar.setCategoria(categoria);
        if (kmAtual != null) veiculoParaSalvar.setKmAtual(kmAtual);
        if (contrato != null) veiculoParaSalvar.setContrato(contrato);

        if (equipeId != null) {
            veiculoParaSalvar.setEquipe(equipeService.buscarPorId(equipeId));
        } else {
            veiculoParaSalvar.setEquipe(null);
        }

        if (centroComandoId != null) {
            veiculoParaSalvar.setCentroComando(centroService.buscarPorId(centroComandoId));
        } else {
            veiculoParaSalvar.setCentroComando(null);
        }

        if (fotoArquivo != null && !fotoArquivo.isEmpty()) {
            if (veiculoParaSalvar.getFotoUrl() != null && veiculoParaSalvar.getFotoUrl().startsWith("http")) {
                storageService.delete(veiculoParaSalvar.getFotoUrl());
            }
            String url = storageService.upload(fotoArquivo, "veiculos");
            veiculoParaSalvar.setFotoUrl(url);
        }
        
        veiculoService.salvar(veiculoParaSalvar);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/foto")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<Void> excluirFoto(@PathVariable UUID id) {
        Veiculo v = veiculoService.buscarPorId(id);
        if (v.getFotoUrl() != null && v.getFotoUrl().startsWith("http")) {
            storageService.delete(v.getFotoUrl());
        }
        v.setFotoUrl(null);
        veiculoService.salvar(v);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        Veiculo v = veiculoService.buscarPorId(id);
        if (v.getFotoUrl() != null && v.getFotoUrl().startsWith("http")) {
            storageService.delete(v.getFotoUrl());
        }
        veiculoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private VeiculoDTO toDTO(Veiculo veiculo) {
        return new VeiculoDTO(
                veiculo.getId(),
                veiculo.getIdentificador(),
                veiculo.getPrefixo(),
                veiculo.getModelo(),
                veiculo.getCategoria(),
                veiculo.getKmAtual(),
                veiculo.getFotoUrl(),
                veiculo.getEquipe() != null ? veiculo.getEquipe().getId() : null,
                veiculo.getCentroComando() != null ? veiculo.getCentroComando().getId() : null,
                veiculo.getContrato()
        );
    }

    @org.springframework.web.bind.annotation.GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public org.springframework.http.ResponseEntity<org.springframework.data.domain.Page<VeiculoDTO>> listarPaginado(
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
        return org.springframework.http.ResponseEntity.ok(veiculoService.listarPaginado(pageable).map(this::toDTO));
    }
}
