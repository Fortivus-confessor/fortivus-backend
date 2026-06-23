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
    private final UsuarioService usuarioService;

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
        Usuario logado = usuarioService.getUsuarioLogado();
        List<Escala> lista;
        if (logado != null && "ROLE_CENTRO_COMANDO".equals(logado.getPerfil().name())) {
            lista = escalaRepository.findAllByCentroComandoIdList(logado.getCentroComando().getId());
        } else {
            lista = escalaRepository.findAllFetched();
        }
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
        
        List<Escala> ativas = escalaRepository.findAtivas();
        
        List<Usuario> aptos = integrantes.stream()
                .filter(u -> {
                    if (u.getEstadoOperacional() == EstadoOperacionalUsuario.FERIAS || 
                        u.getEstadoOperacional() == EstadoOperacionalUsuario.AFASTADO_SAUDE ||
                        u.getEstadoOperacional() == EstadoOperacionalUsuario.LICENCA) return false;
                    
                    if (escala.getDataInicio() == null || escala.getDataFim() == null) return true;
                    long start = escala.getDataInicio().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                    long end = escala.getDataFim().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                    
                    for (Escala e : ativas) {
                        if (e.getId() != null && e.getId().equals(escala.getId())) continue;
                        
                        boolean inScale = (e.getComandante() != null && e.getComandante().getId().equals(u.getId())) || 
                                          (e.getIntegrantes() != null && e.getIntegrantes().stream().anyMatch(i -> i.getId().equals(u.getId())));
                        
                        if (inScale && e.getDataInicio() != null && e.getDataFim() != null) {
                            long eStart = e.getDataInicio().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                            long eEnd = e.getDataFim().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                            
                            if (start <= eEnd && end >= eStart) {
                                return false; // Overlap detected
                            }
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
        
        if (aptos.size() != integrantesIds.size()) {
            throw new RuntimeException("Um ou mais integrantes selecionados estão em Férias/Afastados ou já estão escalados neste período.");
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

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Escala> listarPaginado(org.springframework.data.domain.Pageable pageable) {
        Usuario logado = usuarioService.getUsuarioLogado();
        if (logado != null && "ROLE_CENTRO_COMANDO".equals(logado.getPerfil().name())) {
            return escalaRepository.findAllByCentroComandoId(logado.getCentroComando().getId(), pageable);
        }
        return escalaRepository.findAll(pageable);
    }
}
