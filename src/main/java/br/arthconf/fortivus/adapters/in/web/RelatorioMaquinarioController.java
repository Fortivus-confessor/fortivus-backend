package br.arthconf.fortivus.adapters.in.web;

import br.arthconf.fortivus.application.port.in.BuscarRelatorioMaquinarioUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioMaquinarioUseCase;
import br.arthconf.fortivus.dto.RelatorioMaquinarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/despachos/{despachoId}/relatorios-maquinario")
@RequiredArgsConstructor
public class RelatorioMaquinarioController {

    private final SalvarRelatorioMaquinarioUseCase salvarUseCase;
    private final BuscarRelatorioMaquinarioUseCase buscarUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<RelatorioMaquinarioDTO> salvar(@PathVariable Long despachoId, @RequestBody RelatorioMaquinarioDTO dto) {
        return ResponseEntity.ok(salvarUseCase.salvar(despachoId, dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<RelatorioMaquinarioDTO> buscar(@PathVariable Long despachoId) {
        return buscarUseCase.buscarPorDespachoId(despachoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
