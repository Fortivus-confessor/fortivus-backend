package br.arthconf.fortivus.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
public class HomeController {

    @GetMapping({"/", ""})
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("Fortivus Enterprise API v2");
    }
}
