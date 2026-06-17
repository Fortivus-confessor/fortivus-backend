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
        if (lista != null) {
            lista.forEach(e -> e.getIntegrantes().size());
            return new ArrayList<>(lista);
        }
        return new ArrayList<>();
    }

    @Transactional
    public Escala ativarEscala(Escala escala, List<UUID> integrantesIds) {
        // Busca integrantes selecionados
        List<Usuario> integrantes = usuarioRepository.findAllById(integrantesIds);
        
        // IDs dos integrantes atuais da escala
        java.util.Set<UUID> atuaisIds = escala.getIntegrantes() != null ? 
            escala.getIntegrantes().stream().map(br.arthconf.fortivus.domain.Usuario::getId).collect(Collectors.toSet()) : 
            new java.util.HashSet<>();

        // Regra de negócio: Apenas usuários DISPONÍVEIS ou que JÁ SÃO integrantes DESTA escala podem entrar
        List<Usuario> aptos = integrantes.stream()
                .filter(u -> u.getEstadoOperacional() == EstadoOperacionalUsuario.DISPONIVEL || atuaisIds.contains(u.getId()))
                .collect(Collectors.toList());
        
        if (aptos.size() != integrantesIds.size()) {
            throw new RuntimeException("Um ou mais integrantes selecionados não estão disponíveis (Férias/Afastados).");
        }

        // Se houver integrantes removidos, voltar para DISPONIVEL
        if (escala.getIntegrantes() != null) {
            java.util.Set<UUID> novosIds = aptos.stream().map(br.arthconf.fortivus.domain.Usuario::getId).collect(Collectors.toSet());
            List<Usuario> removidos = escala.getIntegrantes().stream()
                .filter(u -> !novosIds.contains(u.getId()))
                .collect(Collectors.toList());
            removidos.forEach(u -> u.setEstadoOperacional(EstadoOperacionalUsuario.DISPONIVEL));
            usuarioRepository.saveAll(removidos);
            
            escala.getIntegrantes().clear();
            escala.getIntegrantes().addAll(aptos);
        } else {
            escala.setIntegrantes(new java.util.ArrayList<>(aptos));
        }
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

        // Libera os integrantes
        escala.getIntegrantes().forEach(u -> u.setEstadoOperacional(EstadoOperacionalUsuario.DISPONIVEL));
        usuarioRepository.saveAll(escala.getIntegrantes());

        escalaRepository.save(escala);
    }

    @Transactional
    public void deletarEscala(UUID id) {
        Escala escala = escalaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Escala não encontrada"));
        
        if (escala.isAtiva()) {
            escala.getIntegrantes().forEach(u -> u.setEstadoOperacional(EstadoOperacionalUsuario.DISPONIVEL));
            usuarioRepository.saveAll(escala.getIntegrantes());
        }

        escalaRepository.delete(escala);
    }

    @Transactional(readOnly = true)
    public Escala buscarPorId(UUID id) {
        return escalaRepository.findByIdFetched(id)
                .orElseThrow(() -> new RuntimeException("Escala não encontrada"));
    }

    @Transactional
    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000)
    public void verificarEscalasExpiradas() {
        LocalDateTime agora = LocalDateTime.now();
        List<Escala> ativas = escalaRepository.findAtivas();
        if (ativas != null) {
            for (Escala escala : ativas) {
                if (escala.getDataFim() != null && escala.getDataFim().isBefore(agora)) {
                    escala.setAtiva(false);
                    escala.getIntegrantes().forEach(u -> u.setEstadoOperacional(EstadoOperacionalUsuario.DISPONIVEL));
                    usuarioRepository.saveAll(escala.getIntegrantes());
                    escalaRepository.save(escala);
                }
            }
        }
    }
}
