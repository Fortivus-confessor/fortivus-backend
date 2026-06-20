package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.*;
import br.arthconf.fortivus.dto.CadastrarOsDespachoDTO;
import br.arthconf.fortivus.repository.DespachoRepository;
import br.arthconf.fortivus.repository.FocoIncendioRepository;
import br.arthconf.fortivus.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
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
    private final FocoIncendioRepository focoIncendioRepository;
    private final EscalaService escalaService;
    private final UsuarioService usuarioService;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Transactional
    public OrdemServico cadastrarOsEDespacho(CadastrarOsDespachoDTO dto) {
        Long novoId = gerarProximoId();

        OrdemServico os = new OrdemServico();
        os.setId(novoId);
        os.setDescricaoTarefa(dto.descricaoTarefa());
        os.setStatus(dto.status() != null ? dto.status() : SituacaoOrdemServico.ABERTA);
        
        if (dto.eventoFogoId() != null) {
            os.setEventoFogoId(dto.eventoFogoId());
        }

        Escala escala = escalaService.buscarPorId(dto.escalaId());
        os.setEscala(escala);
        
        Usuario relator = usuarioService.buscarPorId(dto.responsavelId());
        os.setRelator(relator);

        if (dto.latitude() != null && dto.longitude() != null) {
            os.setLocalizacaoGeom(geometryFactory.createPoint(new Coordinate(dto.longitude(), dto.latitude())));
        }

        os = ordemServicoRepository.save(os);

        // Criar despacho inicial
        Despacho despacho = new Despacho();
        despacho.setId(gerarProximoIdDespacho());
        despacho.setOrdemServico(os);
        despacho.setEscala(escala);
        despacho.setCategoria(dto.tipoDespacho());
        despacho.setDescricaoTarefa(dto.descricaoTarefa());
        despacho.setStatus(SituacaoDespacho.EM_ANDAMENTO);
        if (dto.latitude() != null && dto.longitude() != null) {
            despacho.setLocalizacaoGeom(geometryFactory.createPoint(new Coordinate(dto.longitude(), dto.latitude())));
        }
        
        despachoRepository.save(despacho);
        
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
        return ordemServicoRepository.findAll();
    }
    
    public OrdemServico buscarPorId(Long id) {
        return ordemServicoRepository.findById(id).orElseThrow(() -> new RuntimeException("OS não encontrada"));
    }

    private Long gerarProximoId() {
        long anoAtual = LocalDateTime.now().getYear();
        long minId = anoAtual * 1000000L; // ex: 2026000000
        long maxId = (anoAtual + 1) * 1000000L;
        
        Long maxExistente = ordemServicoRepository.findMaxIdByAno(minId, maxId).orElse(minId);
        return maxExistente == minId ? minId + 1 : maxExistente + 1;
    }

    private Long gerarProximoIdDespacho() {
        long anoAtual = LocalDateTime.now().getYear();
        long minId = anoAtual * 1000000L;
        long maxId = (anoAtual + 1) * 1000000L;
        
        Long maxExistente = despachoRepository.findMaxIdByAno(minId, maxId).orElse(minId);
        return maxExistente == minId ? minId + 1 : maxExistente + 1;
    }
}
