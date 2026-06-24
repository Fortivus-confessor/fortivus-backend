package br.arthconf.fortivus.service;

import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;

import br.arthconf.fortivus.domain.*;
import br.arthconf.fortivus.domain.model.Usuario;
import br.arthconf.fortivus.dto.CadastrarOsDespachoDTO;
import br.arthconf.fortivus.repository.DespachoRepository;
import br.arthconf.fortivus.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final DespachoRepository despachoRepository;
    private final EscalaService escalaService;
    private final UsuarioService usuarioService;


    @Transactional
    public OrdemServico cadastrarOsEDespacho(CadastrarOsDespachoDTO dto) {
        Long novoId = gerarProximoId();

        OrdemServico os = new OrdemServico();
        os.setId(novoId);
        os.setDescricaoTarefa(dto.descricaoTarefa());
        os.setStatus(SituacaoOrdemServico.EM_EXECUCAO); // Regra de negócio: Sempre nasce em execução
        
        if (dto.eventoFogoId() != null) {
            os.setEventoFogoId(dto.eventoFogoId());
        }

        Escala escala = escalaService.buscarPorId(dto.escalaId());
        os.setEscala(escala);
        
        Usuario relator = usuarioService.buscarPorId(dto.responsavelId());
        os.setRelator(br.arthconf.fortivus.infrastructure.persistence.mapper.UsuarioMapper.toEntity(relator));



        os = ordemServicoRepository.save(os);

        // Criar DespachoEntity inicial
        DespachoEntity DespachoEntity = new DespachoEntity();
        DespachoEntity.setId(gerarProximoIdDespacho());
        DespachoEntity.setOrdemServico(os);
        DespachoEntity.setEscala(escala);
        DespachoEntity.setCategoria(dto.tipoDespacho());
        DespachoEntity.setDescricaoTarefa(dto.descricaoTarefa());
        DespachoEntity.setStatus(SituacaoDespacho.EM_ANDAMENTO);
        if (dto.latitude() != null && dto.longitude() != null) {
            DespachoEntity.setLatitude(dto.latitude());
            DespachoEntity.setLongitude(dto.longitude());
        }
        
        despachoRepository.save(DespachoEntity);
        
        return os;
    }
    
    @Transactional
    public OrdemServico editarOrdemServico(Long id, CadastrarOsDespachoDTO dto) {
        OrdemServico os = ordemServicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ordem de serviço não encontrada"));
            
        os.setDescricaoTarefa(dto.descricaoTarefa());
        if (dto.eventoFogoId() != null) {
            os.setEventoFogoId(dto.eventoFogoId());
        } else {
            os.setEventoFogoId(null);
        }
        
        return ordemServicoRepository.save(os);
    }
    
    public List<OrdemServico> listarTodas() {
        Usuario logado = usuarioService.getUsuarioLogado();
        if (logado != null) {
            String role = logado.getPerfil().name();
            if ("ROLE_CENTRO_COMANDO".equals(role)) {
                return ordemServicoRepository.findAllByCentroComandoIdList(logado.getCentroComando().getId());
            } else if ("ROLE_COMBATENTE".equals(role)) {
                return ordemServicoRepository.findAllByCombatenteIdList(logado.getId());
            }
        }
        return ordemServicoRepository.findAll();
    }
    
    public OrdemServico buscarPorId(Long id) {
        return ordemServicoRepository.findById(id).orElseThrow(() -> new RuntimeException("OS não encontrada"));
    }

    private Long gerarProximoId() {
        long anoAtual = LocalDateTime.now().getYear();
        long minId = anoAtual * 100000000L; // ex: 202600000000
        long maxId = (anoAtual + 1) * 100000000L;
        
        Long maxExistente = ordemServicoRepository.findMaxIdByAno(minId, maxId).orElse(minId);
        return maxExistente == minId ? minId + 1 : maxExistente + 1;
    }

    @Transactional
    public void excluirOrdemServico(Long id) {
        OrdemServico os = ordemServicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ordem de serviço não encontrada"));
            
        if (!os.getDespachos().isEmpty()) {
            throw new RuntimeException("Não é possível excluir a OS pois ela possui despachos atrelados.");
        }
        
        ordemServicoRepository.delete(os);
    }

    private Long gerarProximoIdDespacho() {
        long anoAtual = LocalDateTime.now().getYear();
        long minId = anoAtual * 100000000L;
        long maxId = (anoAtual + 1) * 100000000L;
        
        Long maxExistente = despachoRepository.findMaxIdByAno(minId, maxId).orElse(minId);
        return maxExistente == minId ? minId + 1 : maxExistente + 1;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<OrdemServico> listarPaginado(org.springframework.data.domain.Pageable pageable) {
        Usuario logado = usuarioService.getUsuarioLogado();
        if (logado != null) {
            String role = logado.getPerfil().name();
            if ("ROLE_CENTRO_COMANDO".equals(role)) {
                return ordemServicoRepository.findAllByCentroComandoId(logado.getCentroComando().getId(), pageable);
            } else if ("ROLE_COMBATENTE".equals(role)) {
                return ordemServicoRepository.findAllByCombatenteId(logado.getId(), pageable);
            }
        }
        return ordemServicoRepository.findAll(pageable);
    }
}


