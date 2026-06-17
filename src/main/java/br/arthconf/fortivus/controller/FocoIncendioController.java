package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.dto.FocoIncendioDTO;
import br.arthconf.fortivus.service.FocoIncendioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/focos")
@RequiredArgsConstructor
public class FocoIncendioController {

    private final FocoIncendioService service;

    @GetMapping
    public ResponseEntity<List<FocoIncendioDTO>> listarFocos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<FocoIncendioDTO>> listarFocosAtivos() {
        return ResponseEntity.ok(service.listarFocosAtivos());
    }

    @PostMapping
    public ResponseEntity<FocoIncendioDTO> registrarFocoManual(@RequestBody FocoIncendioDTO dto) {
        FocoIncendioDTO salvo = service.registrarFocoManual(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }
}
