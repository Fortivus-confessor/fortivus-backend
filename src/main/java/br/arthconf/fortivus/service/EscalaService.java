package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.Escala;
import br.arthconf.fortivus.domain.EstadoOperacionalUsuario;
import br.arthconf.fortivus.domain.Usuario;
import br.arthconf.fortivus.repository.EscalaRepository;
import br.arthconf.fortivus.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EscalaService {

    private final EscalaRepository escalaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Escala> listarAtivas() {
        var lista = escalaRepository.findAtivas();
        if (lista != null) {
            lista.forEach(e -> e.getIntegrantes().size()); // Touch para carregar coleção ManyToMany
            return new ArrayList<>(lista);
        }
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<Escala> listarTodas() {
        var lista = escalaRepository.findAll();
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Transactional
    public Escala ativarEscala(Escala escala, List<UUID> integrantesIds) {
        // Busca e valida integrantes selecionados
        List<Usuario> integrantes = usuarioRepository.findAllById(integrantesIds);
        
        // Regra de negócio: Apenas usuários DISPONÍVEIS podem entrar na escala
        List<Usuario> aptos = integrantes.stream()
                .filter(u -> u.getEstadoOperacional() == EstadoOperacionalUsuario.DISPONIVEL)
                .collect(Collectors.toList());
        
        if (aptos.size() != integrantesIds.size()) {
            throw new RuntimeException("Um ou mais integrantes selecionados não estão disponíveis (Férias/Afastados).");
        }

        escala.setIntegrantes(aptos);
        escala.setAtiva(true);

        // Atualiza estado dos integrantes para EM_MISSAO
        aptos.forEach(u -> u.setEstadoOperacional(EstadoOperacionalUsuario.EM_MISSAO));
        usuarioRepository.saveAll(aptos);

        return escalaRepository.save(escala);
    }

    @Transactional
    public void encerrarEscala(UUID id) {
        Escala escala = escalaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Escala não encontrada"));
        
        escala.setAtiva(false);
        escala.setDataFim(LocalDateTime.now());

        // Libera os integrantes
        escala.getIntegrantes().forEach(u -> u.setEstadoOperacional(EstadoOperacionalUsuario.DISPONIVEL));
        usuarioRepository.saveAll(escala.getIntegrantes());

        escalaRepository.save(escala);
    }

    @Transactional(readOnly = true)
    public Escala buscarPorId(UUID id) {
        return escalaRepository.findByIdFetched(id)
                .orElseThrow(() -> new RuntimeException("Escala não encontrada"));
    }
}
