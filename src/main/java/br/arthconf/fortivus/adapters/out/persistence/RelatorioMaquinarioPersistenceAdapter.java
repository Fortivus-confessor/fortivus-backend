package br.arthconf.fortivus.adapters.out.persistence;

import br.arthconf.fortivus.application.port.out.RelatorioMaquinarioPort;
import br.arthconf.fortivus.domain.model.RelatorioMaquinario;
import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioMaquinarioEntity;
import br.arthconf.fortivus.infrastructure.persistence.mapper.RelatorioMaquinarioMapper;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataDespachoRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RelatorioMaquinarioPersistenceAdapter implements RelatorioMaquinarioPort {

    private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

    private final RelatorioMaquinarioRepository repository;
    private final SpringDataDespachoRepository despachoRepository;

    @Override
    @Transactional
    public RelatorioMaquinario salvar(RelatorioMaquinario domain) {
        Long id = domain.getDespachoId();

        DespachoEntity managed = despachoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despacho não encontrado: " + id));

        RelatorioMaquinarioEntity relatorio = repository.findById(id).orElseGet(() -> {
            RelatorioMaquinarioEntity novo = new RelatorioMaquinarioEntity();
            novo.setDespacho(managed);
            return novo;
        });

        relatorio.setDespacho(managed);
        relatorio.setId(managed.getId());
        relatorio.setHorimetroInicial(domain.getHorimetroInicial());
        relatorio.setHorimetroFinal(domain.getHorimetroFinal());
        relatorio.setTempoLiquido(domain.getTempoLiquido());
        relatorio.setHoraInicioOperacao(domain.getHoraInicioOperacao());
        relatorio.setHoraFimOperacao(domain.getHoraFimOperacao());
        relatorio.setTiposEmprego(domain.getTiposEmprego());
        relatorio.setComprimentoAceiros(domain.getComprimentoAceiros());
        relatorio.setDescricaoOutroEmprego(domain.getDescricaoOutroEmprego());
        relatorio.setEfetividadeCombate(domain.getEfetividadeCombate());
        relatorio.setNecessidadeReforco(domain.getNecessidadeReforco());
        relatorio.setTiposReforcoNecessarios(domain.getTiposReforcoNecessarios());
        relatorio.setHistoricoDescritivo(domain.getHistoricoDescritivo());
        relatorio.setResultadoOcorrencia(domain.getResultadoOcorrencia());
        relatorio.setOutroResultadoDescricao(domain.getOutroResultadoDescricao());

        if (domain.getDataInicio() != null) {
            relatorio.setDataInicio(domain.getDataInicio());
        } else if (relatorio.getDataInicio() == null) {
            relatorio.setDataInicio(LocalDateTime.now());
        }
        relatorio.setDataFim(domain.getDataFim() != null ? domain.getDataFim() : LocalDateTime.now());

        if (domain.getAreaAtuacaoLat() != null && domain.getAreaAtuacaoLng() != null) {
            relatorio.setAreaAtuacaoGeom(
                    GF.createPoint(new Coordinate(domain.getAreaAtuacaoLng(), domain.getAreaAtuacaoLat())));
        }

        return RelatorioMaquinarioMapper.toDomain(repository.save(relatorio));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RelatorioMaquinario> buscarPorDespachoId(Long despachoId) {
        return repository.findById(despachoId).map(RelatorioMaquinarioMapper::toDomain);
    }
}
