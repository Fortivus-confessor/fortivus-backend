package br.arthconf.fortivus.adapters.in.web;

import br.arthconf.fortivus.application.port.in.BuscarRelatorioAereoUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioAereoUseCase;
import br.arthconf.fortivus.dto.RelatorioAereoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/despachos/{despachoId}/relatorios-aereos")
@RequiredArgsConstructor
public class RelatorioAereoController {

    private final SalvarRelatorioAereoUseCase salvarUseCase;
    private final BuscarRelatorioAereoUseCase buscarUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<RelatorioAereoDTO> salvar(@PathVariable Long despachoId, @RequestBody RelatorioAereoDTO dto) {
        return ResponseEntity.ok(salvarUseCase.salvar(despachoId, dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<RelatorioAereoDTO> buscar(@PathVariable Long despachoId) {
        return buscarUseCase.buscarPorDespachoId(despachoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
