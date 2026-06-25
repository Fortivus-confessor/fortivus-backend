package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.application.port.in.ListarCheckoutsPorEscalaUseCase;
import br.arthconf.fortivus.application.port.in.RegistrarDevolucaoUseCase;
import br.arthconf.fortivus.application.port.in.RegistrarEmprestimoUseCase;
import br.arthconf.fortivus.application.port.out.UsuarioPort;
import br.arthconf.fortivus.domain.model.CheckoutEquipamento;
import br.arthconf.fortivus.dto.CheckoutEquipamentoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ativos/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final ListarCheckoutsPorEscalaUseCase listarCheckoutsUseCase;
    private final RegistrarEmprestimoUseCase registrarEmprestimoUseCase;
    private final RegistrarDevolucaoUseCase registrarDevolucaoUseCase;
    private final UsuarioPort usuarioPort;

    @GetMapping("/{escalaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<List<CheckoutEquipamentoDTO>> listarPorEscala(@PathVariable UUID escalaId) {
        return ResponseEntity.ok(
                listarCheckoutsUseCase.executar(escalaId).stream().map(this::toDTO).toList()
        );
    }

    @PostMapping("/{escalaId}/emprestar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<Void> emprestar(@PathVariable UUID escalaId,
                                          @RequestParam UUID equipamentoId,
                                          @RequestParam String emailResponsavel) {
        UUID responsavelId = usuarioPort.findByEmailIgnoreCase(emailResponsavel)
                .map(u -> u.getId())
                .orElseThrow(() -> new RuntimeException("Responsável não encontrado: " + emailResponsavel));

        registrarEmprestimoUseCase.executar(escalaId, equipamentoId, responsavelId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{escalaId}/devolver/{checkoutId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<Void> devolver(@PathVariable UUID escalaId,
                                         @PathVariable UUID checkoutId,
                                         @RequestParam String emailResponsavel) {
        UUID responsavelId = usuarioPort.findByEmailIgnoreCase(emailResponsavel)
                .map(u -> u.getId())
                .orElseThrow(() -> new RuntimeException("Responsável não encontrado: " + emailResponsavel));

        registrarDevolucaoUseCase.executar(checkoutId, responsavelId);
        return ResponseEntity.ok().build();
    }

    private CheckoutEquipamentoDTO toDTO(CheckoutEquipamento checkout) {
        return new CheckoutEquipamentoDTO(
                checkout.getId(),
                checkout.getEscalaId(),
                checkout.getEquipamentoId(),
                checkout.getResponsavelEntregaId(),
                checkout.getDataEmprestimo(),
                checkout.getDataDevolucao(),
                checkout.getResponsavelRecebimentoId()
        );
    }
}
