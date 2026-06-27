package br.arthconf.fortivus.infrastructure.persistence.mapper;

import br.arthconf.fortivus.domain.model.RelatorioAereo;
import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioAereoEntity;

import java.util.ArrayList;

public class RelatorioAereoMapper {

    private RelatorioAereoMapper() {}

    public static RelatorioAereo toDomain(RelatorioAereoEntity entity) {
        Double lat = null, lng = null;
        if (entity.getAreaAtuacaoGeom() != null) {
            lat = entity.getAreaAtuacaoGeom().getCoordinate().y;
            lng = entity.getAreaAtuacaoGeom().getCoordinate().x;
        }
        return RelatorioAereo.builder()
                .despachoId(entity.getId())
                .horimetroInicial(entity.getHorimetroInicial())
                .horimetroFinal(entity.getHorimetroFinal())
                .horasLiquidas(entity.getHorasLiquidas())
                .tiposEmprego(entity.getTiposEmprego() != null ? new ArrayList<>(entity.getTiposEmprego()) : null)
                .areaAtuacaoLat(lat)
                .areaAtuacaoLng(lng)
                .qtdeLancamentos(entity.getQtdeLancamentos())
                .houveUsoAgua(entity.getHouveUsoAgua())
                .volumeAguaLitros(entity.getVolumeAguaLitros())
                .origensAgua(entity.getOrigensAgua() != null ? new ArrayList<>(entity.getOrigensAgua()) : null)
                .outraOrigemAguaDescricao(entity.getOutraOrigemAguaDescricao())
                .efetividadeCombate(entity.getEfetividadeCombate())
                .necessidadeReforco(entity.getNecessidadeReforco())
                .tiposReforcoNecessarios(entity.getTiposReforcoNecessarios() != null ? new ArrayList<>(entity.getTiposReforcoNecessarios()) : null)
                .historicoDescritivo(entity.getHistoricoDescritivo())
                .resultadoOcorrencia(entity.getResultadoOcorrencia())
                .outroResultadoDescricao(entity.getOutroResultadoDescricao())
                .dataInicio(entity.getDataInicio())
                .dataFim(entity.getDataFim())
                .build();
    }
}
