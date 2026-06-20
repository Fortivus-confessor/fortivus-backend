package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.EventoFogo;
import br.arthconf.fortivus.dto.EventoFogoDTO;
import br.arthconf.fortivus.repository.EventoFogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/eventos-fogo")
@RequiredArgsConstructor
public class EventoFogoController {

    private final EventoFogoRepository eventoFogoRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<List<EventoFogoDTO>> listar() {
        return ResponseEntity.ok(eventoFogoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CENTRO_COMANDO_CENTRAL', 'CENTRO_COMANDO', 'COMBATENTE')")
    public ResponseEntity<List<EventoFogoDTO>> buscarPorCodigo(@RequestParam String q) {
        return ResponseEntity.ok(eventoFogoRepository.findByCodigoContainingIgnoreCase(q).stream()
                .map(this::toDTO)
                .collect(Collectors.toList()));
    }

    private EventoFogoDTO toDTO(EventoFogo eventoFogo) {
        return new EventoFogoDTO(
                eventoFogo.getId(),
                eventoFogo.getCodigo(),
                eventoFogo.getDescricao(),
                eventoFogo.getLatitude(),
                eventoFogo.getLongitude(),
                eventoFogo.getDataCriacao(),
                eventoFogo.getStatus()
        );
    }
}
