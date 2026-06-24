package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.Escala;
import br.arthconf.fortivus.domain.model.Usuario;
import br.arthconf.fortivus.domain.CheckoutEquipamento;
import br.arthconf.fortivus.dto.CheckoutEquipamentoDTO;
import br.arthconf.fortivus.service.CheckoutService;
import br.arthconf.fortivus.service.EscalaService;
import br.arthconf.fortivus.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ativos/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final EscalaService escalaService;
    private final UsuarioService usuarioService;

    @GetMapping("/{escalaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<List<CheckoutEquipamentoDTO>> listarPorEscala(@PathVariable UUID escalaId) {
        List<CheckoutEquipamentoDTO> checkouts = checkoutService.listarPorEscala(escalaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(checkouts);
    }

    @PostMapping("/{escalaId}/emprestar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<Void> emprestar(@PathVariable UUID escalaId, 
                                          @RequestParam UUID equipamentoId,
                                          @RequestParam String emailResponsavel) {
        Escala escala = escalaService.buscarPorId(escalaId);
        Usuario responsavel = usuarioService.listarTodos().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(emailResponsavel))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Responsável não encontrado"));

        checkoutService.registrarEmprestimo(escala, equipamentoId, responsavel);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{escalaId}/devolver/{checkoutId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO')")
    public ResponseEntity<Void> devolver(@PathVariable UUID escalaId,
                                         @PathVariable UUID checkoutId,
                                         @RequestParam String emailResponsavel) {
        Usuario responsavel = usuarioService.listarTodos().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(emailResponsavel))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Responsável não encontrado"));

        checkoutService.registrarDevolucao(checkoutId, responsavel);
        return ResponseEntity.ok().build();
    }

    private CheckoutEquipamentoDTO toDTO(CheckoutEquipamento checkout) {
        return new CheckoutEquipamentoDTO(
                checkout.getId(),
                checkout.getEscala() != null ? checkout.getEscala().getId() : null,
                checkout.getEquipamento() != null ? checkout.getEquipamento().getId() : null,
                checkout.getResponsavelEntrega() != null ? checkout.getResponsavelEntrega().getId() : null,
                checkout.getDataEmprestimo(),
                checkout.getDataDevolucao(),
                checkout.getResponsavelRecebimento() != null ? checkout.getResponsavelRecebimento().getId() : null
        );
    }
}
