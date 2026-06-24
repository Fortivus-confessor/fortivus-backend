package br.arthconf.fortivus.service;

import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.repository.DespachoRepository;
import br.arthconf.fortivus.repository.RelatorioTerrestreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.arthconf.fortivus.application.usecase.CriarDespachoUseCase;
import br.arthconf.fortivus.application.usecase.AtualizarStatusDespachoUseCase;
import br.arthconf.fortivus.domain.model.Despacho;
import br.arthconf.fortivus.infrastructure.persistence.mapper.DespachoMapper;
@Service
@RequiredArgsConstructor
public class DespachoService {

    private final DespachoRepository despachoRepository;
    private final RelatorioTerrestreRepository relatorioTerrestreRepository;
    private final UsuarioService usuarioService;
    private final CriarDespachoUseCase criarDespachoUseCase;
    private final AtualizarStatusDespachoUseCase atualizarStatusDespachoUseCase;
    private final DespachoMapper mapper;

    @Transactional(readOnly = true)
    public List<DespachoEntity> listarTodos() {
        br.arthconf.fortivus.domain.model.Usuario logado = usuarioService.getUsuarioLogado();
        List<DespachoEntity> lista;
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
        atualizarStatusDespachoUseCase.executar(id, novoStatus);
    }

    @Transactional
    public DespachoEntity salvar(DespachoEntity DespachoEntity) {
        Despacho dominio = mapper.toDomain(DespachoEntity);
        Despacho salvo = criarDespachoUseCase.executar(dominio);
        return despachoRepository.findById(salvo.getId()).orElse(null);
    }

    @Transactional
    public void deletar(Long id) {
        if (relatorioTerrestreRepository.existsById(id)) {
            relatorioTerrestreRepository.deleteById(id);
        }
        despachoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public DespachoEntity buscarPorId(Long id) {
        return despachoRepository.findByIdFetched(id).orElse(null);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<DespachoEntity> listarPaginado(org.springframework.data.domain.Pageable pageable) {
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

