package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.application.port.in.BuscarCentroComandoPorIdUseCase;
import br.arthconf.fortivus.application.port.in.GerenciarEquipeUseCase;
import br.arthconf.fortivus.application.port.in.GerenciarVeiculoUseCase;
import br.arthconf.fortivus.application.port.in.ListarVeiculosUseCase;
import br.arthconf.fortivus.domain.model.CategoriaOperacao;
import br.arthconf.fortivus.domain.model.Veiculo;
import br.arthconf.fortivus.dto.VeiculoDTO;
import br.arthconf.fortivus.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ativos/frota")
@RequiredArgsConstructor
public class VeiculoController {

    private final ListarVeiculosUseCase listarVeiculosUseCase;
    private final GerenciarVeiculoUseCase gerenciarVeiculoUseCase;
    private final GerenciarEquipeUseCase gerenciarEquipeUseCase;
    private final BuscarCentroComandoPorIdUseCase buscarCentroUseCase;
    private final FileStorageService storageService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<List<VeiculoDTO>> listar() {
        List<VeiculoDTO> veiculos = listarVeiculosUseCase.listarTodos().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(veiculos);
    }

    @GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<Page<VeiculoDTO>> listarPaginado(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(listarVeiculosUseCase.listarPaginado(pageable).map(this::toDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<VeiculoDTO> buscarPorId(@PathVariable UUID id) {
        Veiculo veiculo = gerenciarVeiculoUseCase.buscarPorId(id);
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

        CategoriaOperacao categoria = null;
        if (categoriaStr != null && !categoriaStr.isEmpty()) {
            categoria = CategoriaOperacao.fromString(categoriaStr);
        }

        Veiculo veiculoParaSalvar;
        if (id != null) {
            veiculoParaSalvar = gerenciarVeiculoUseCase.buscarPorId(id);
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
            veiculoParaSalvar.setEquipe(gerenciarEquipeUseCase.buscarPorId(equipeId));
        } else {
            veiculoParaSalvar.setEquipe(null);
        }

        if (centroComandoId != null) {
            veiculoParaSalvar.setCentroComando(buscarCentroUseCase.executar(centroComandoId)
                    .orElseThrow(() -> new RuntimeException("Centro de Comando não encontrado")));
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

        gerenciarVeiculoUseCase.salvar(veiculoParaSalvar);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/foto")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<Void> excluirFoto(@PathVariable UUID id) {
        Veiculo v = gerenciarVeiculoUseCase.buscarPorId(id);
        if (v.getFotoUrl() != null && v.getFotoUrl().startsWith("http")) {
            storageService.delete(v.getFotoUrl());
        }
        v.setFotoUrl(null);
        gerenciarVeiculoUseCase.salvar(v);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        Veiculo v = gerenciarVeiculoUseCase.buscarPorId(id);
        if (v.getFotoUrl() != null && v.getFotoUrl().startsWith("http")) {
            storageService.delete(v.getFotoUrl());
        }
        gerenciarVeiculoUseCase.deletar(id);
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
}
