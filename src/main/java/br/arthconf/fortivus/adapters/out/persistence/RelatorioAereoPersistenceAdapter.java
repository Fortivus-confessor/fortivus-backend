package br.arthconf.fortivus.adapters.out.persistence;

import br.arthconf.fortivus.application.port.out.RelatorioAereoPort;
import br.arthconf.fortivus.domain.model.RelatorioAereo;
import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioAereoEntity;
import br.arthconf.fortivus.infrastructure.persistence.mapper.RelatorioAereoMapper;
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
public class RelatorioAereoPersistenceAdapter implements RelatorioAereoPort {

    private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

    private final RelatorioAereoRepository repository;
    private final SpringDataDespachoRepository despachoRepository;

    @Override
    @Transactional
    public RelatorioAereo salvar(RelatorioAereo domain) {
        Long id = domain.getDespachoId();

        DespachoEntity managed = despachoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despacho não encontrado: " + id));

        RelatorioAereoEntity relatorio = repository.findById(id).orElseGet(() -> {
            RelatorioAereoEntity novo = new RelatorioAereoEntity();
            novo.setDespacho(managed);
            return novo;
        });

        relatorio.setDespacho(managed);
        relatorio.setId(managed.getId());
        relatorio.setHorimetroInicial(domain.getHorimetroInicial());
        relatorio.setHorimetroFinal(domain.getHorimetroFinal());
        relatorio.setHorasLiquidas(domain.getHorasLiquidas());
        relatorio.setTiposEmprego(domain.getTiposEmprego());
        relatorio.setQtdeLancamentos(domain.getQtdeLancamentos());
        relatorio.setHouveUsoAgua(domain.getHouveUsoAgua());
        relatorio.setVolumeAguaLitros(domain.getVolumeAguaLitros());
        relatorio.setOrigensAgua(domain.getOrigensAgua());
        relatorio.setOutraOrigemAguaDescricao(domain.getOutraOrigemAguaDescricao());
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

        return RelatorioAereoMapper.toDomain(repository.save(relatorio));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RelatorioAereo> buscarPorDespachoId(Long despachoId) {
        return repository.findById(despachoId).map(RelatorioAereoMapper::toDomain);
    }
}
