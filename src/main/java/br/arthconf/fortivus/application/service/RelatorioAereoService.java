package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.BuscarRelatorioAereoUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioAereoUseCase;
import br.arthconf.fortivus.application.port.out.RelatorioAereoPort;
import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioAereoEntity;
import br.arthconf.fortivus.dto.RelatorioAereoDTO;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RelatorioAereoService implements SalvarRelatorioAereoUseCase, BuscarRelatorioAereoUseCase {

    private final RelatorioAereoPort relatorioAereoPort;

    @Override
    @Transactional
    public RelatorioAereoDTO salvar(Long despachoId, RelatorioAereoDTO dto) {
        RelatorioAereoEntity relatorio = relatorioAereoPort.buscarPorDespachoId(despachoId)
                .orElseGet(() -> {
                    br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity stub = new br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity();
                    stub.setId(despachoId);
                    RelatorioAereoEntity novo = new RelatorioAereoEntity();
                    novo.setDespacho(stub);
                    return novo;
                });
        relatorio.setId(despachoId);
        relatorio.setHorimetroInicial(dto.horimetroInicial());
        relatorio.setHorimetroFinal(dto.horimetroFinal());
        relatorio.setHorasLiquidas(dto.horasLiquidas());
        relatorio.setTiposEmprego(dto.tiposEmprego());
        relatorio.setQtdeLancamentos(dto.qtdeLancamentos());
        relatorio.setHouveUsoAgua(dto.houveUsoAgua());
        relatorio.setVolumeAguaLitros(dto.volumeAguaLitros());
        relatorio.setOrigensAgua(dto.origensAgua());
        relatorio.setOutraOrigemAguaDescricao(dto.outraOrigemAguaDescricao());
        relatorio.setEfetividadeCombate(dto.efetividadeCombate());
        relatorio.setNecessidadeReforco(dto.necessidadeReforco());
        relatorio.setTiposReforcoNecessarios(dto.tiposReforcoNecessarios());
        relatorio.setHistoricoDescritivo(dto.historicoDescritivo());
        relatorio.setResultadoOcorrencia(dto.resultadoOcorrencia());
        relatorio.setOutroResultadoDescricao(dto.outroResultadoDescricao());

        if (dto.dataInicio() != null) {
            relatorio.setDataInicio(dto.dataInicio());
        } else if (relatorio.getDataInicio() == null) {
            relatorio.setDataInicio(LocalDateTime.now());
        }
        relatorio.setDataFim(dto.dataFim() != null ? dto.dataFim() : LocalDateTime.now());

        if (dto.areaAtuacaoLat() != null && dto.areaAtuacaoLng() != null) {
            GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
            relatorio.setAreaAtuacaoGeom(gf.createPoint(new Coordinate(dto.areaAtuacaoLng(), dto.areaAtuacaoLat())));
        }

        RelatorioAereoEntity salvo = relatorioAereoPort.salvar(relatorio);
        return toDTO(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RelatorioAereoDTO> buscarPorDespachoId(Long despachoId) {
        return relatorioAereoPort.buscarPorDespachoId(despachoId).map(this::toDTO);
    }

    private RelatorioAereoDTO toDTO(RelatorioAereoEntity rel) {
        Double lat = null, lng = null;
        if (rel.getAreaAtuacaoGeom() != null) {
            lat = rel.getAreaAtuacaoGeom().getCoordinate().y;
            lng = rel.getAreaAtuacaoGeom().getCoordinate().x;
        }
        return new RelatorioAereoDTO(
            rel.getId(),
            rel.getHorimetroInicial(),
            rel.getHorimetroFinal(),
            rel.getHorasLiquidas(),
            rel.getTiposEmprego() != null ? new java.util.ArrayList<>(rel.getTiposEmprego()) : null,
            lat,
            lng,
            rel.getQtdeLancamentos(),
            rel.getHouveUsoAgua(),
            rel.getVolumeAguaLitros(),
            rel.getOrigensAgua() != null ? new java.util.ArrayList<>(rel.getOrigensAgua()) : null,
            rel.getOutraOrigemAguaDescricao(),
            rel.getEfetividadeCombate(),
            rel.getNecessidadeReforco(),
            rel.getTiposReforcoNecessarios() != null ? new java.util.ArrayList<>(rel.getTiposReforcoNecessarios()) : null,
            rel.getHistoricoDescritivo(),
            rel.getResultadoOcorrencia(),
            rel.getOutroResultadoDescricao(),
            rel.getDataInicio(),
            rel.getDataFim()
        );
    }
}
