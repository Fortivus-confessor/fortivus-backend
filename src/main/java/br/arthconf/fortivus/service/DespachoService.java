package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.Despacho;
import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.repository.DespachoRepository;
import br.arthconf.fortivus.repository.RelatorioTerrestreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DespachoService {

    private final DespachoRepository despachoRepository;
    private final RelatorioTerrestreRepository relatorioTerrestreRepository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<Despacho> listarTodos() {
        br.arthconf.fortivus.domain.model.Usuario logado = usuarioService.getUsuarioLogado();
        List<Despacho> lista;
        if (logado != null) {
            String role = logado.getPerfil().name();
            if ("ROLE_CENTRO_COMANDO".equals(role)) {
                lista = despachoRepository.findAllByCentroComandoIdList(logado.getCentroComando().getId());
            } else if ("ROLE_COMBATENTE".equals(role)) {
                lista = despachoRepository.findAllByCombatenteIdList(logado.getId());
            } else {
                lista = despachoRepository.findAllWithDetails();
            }
        } else {
            lista = despachoRepository.findAllWithDetails();
        }
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Transactional
    public void atualizarStatus(Long id, SituacaoDespacho novoStatus) {
        var despacho = despachoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despacho não encontrado"));
                
        br.arthconf.fortivus.domain.model.Usuario logado = usuarioService.getUsuarioLogado();
        if (logado != null && "ROLE_COMBATENTE".equals(logado.getPerfil().name())) {
            boolean isIntegrante = false;
            if (despacho.getEscala() != null) {
                if (despacho.getEscala().getComandante().getId().equals(logado.getId())) {
                    isIntegrante = true;
                } else {
                    isIntegrante = despacho.getEscala().getIntegrantes().stream().anyMatch(i -> i.getId().equals(logado.getId()));
                }
            }
            if (!isIntegrante) {
                throw new org.springframework.security.access.AccessDeniedException("Você só pode atualizar o status dos seus próprios despachos.");
            }
        }
        
        despacho.setStatus(novoStatus);
        if (novoStatus == SituacaoDespacho.CONCLUIDO) {
            despacho.setDataFim(LocalDateTime.now(java.time.ZoneId.of("America/Sao_Paulo")));
        }
        despachoRepository.save(despacho);
    }

    @Transactional
    public Despacho salvar(Despacho despacho) {
        if (despacho.getId() == null) {
            long anoAtual = LocalDateTime.now().getYear();
            long minId = anoAtual * 100000000L;
            long maxId = (anoAtual + 1) * 100000000L;
            Long maxExistente = despachoRepository.findMaxIdByAno(minId, maxId).orElse(minId);
            despacho.setId(maxExistente == minId ? minId + 1 : maxExistente + 1);
            despacho.setDataInicio(LocalDateTime.now(java.time.ZoneId.of("America/Sao_Paulo")));
        }
        
        // Regra de negócio: Adicionar um novo despacho reativa a OS para EM_EXECUCAO
        if (despacho.getOrdemServico() != null && despacho.getOrdemServico().getStatus() == br.arthconf.fortivus.domain.SituacaoOrdemServico.CONCLUIDA) {
            despacho.getOrdemServico().setStatus(br.arthconf.fortivus.domain.SituacaoOrdemServico.EM_EXECUCAO);
        }
        
        return despachoRepository.save(despacho);
    }

    @Transactional
    public void deletar(Long id) {
        if (relatorioTerrestreRepository.existsById(id)) {
            relatorioTerrestreRepository.deleteById(id);
        }
        despachoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Despacho buscarPorId(Long id) {
        return despachoRepository.findByIdFetched(id).orElse(null);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Despacho> listarPaginado(org.springframework.data.domain.Pageable pageable) {
        br.arthconf.fortivus.domain.model.Usuario logado = usuarioService.getUsuarioLogado();
        if (logado != null) {
            String role = logado.getPerfil().name();
            if ("ROLE_CENTRO_COMANDO".equals(role)) {
                return despachoRepository.findAllByCentroComandoId(logado.getCentroComando().getId(), pageable);
            } else if ("ROLE_COMBATENTE".equals(role)) {
                return despachoRepository.findAllByCombatenteId(logado.getId(), pageable);
            }
        }
        return despachoRepository.findAll(pageable);
    }
}
