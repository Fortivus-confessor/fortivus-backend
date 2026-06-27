package br.arthconf.fortivus.infrastructure.persistence.mapper;

import br.arthconf.fortivus.domain.model.RelatorioMaquinario;
import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioMaquinarioEntity;

import java.util.ArrayList;

public class RelatorioMaquinarioMapper {

    private RelatorioMaquinarioMapper() {}

    public static RelatorioMaquinario toDomain(RelatorioMaquinarioEntity entity) {
        Double lat = null, lng = null;
        if (entity.getAreaAtuacaoGeom() != null) {
            lat = entity.getAreaAtuacaoGeom().getCoordinate().y;
            lng = entity.getAreaAtuacaoGeom().getCoordinate().x;
        }
        return RelatorioMaquinario.builder()
                .despachoId(entity.getId())
                .horimetroInicial(entity.getHorimetroInicial())
                .horimetroFinal(entity.getHorimetroFinal())
                .tempoLiquido(entity.getTempoLiquido())
                .horaInicioOperacao(entity.getHoraInicioOperacao())
                .horaFimOperacao(entity.getHoraFimOperacao())
                .tiposEmprego(entity.getTiposEmprego() != null ? new ArrayList<>(entity.getTiposEmprego()) : null)
                .comprimentoAceiros(entity.getComprimentoAceiros())
                .descricaoOutroEmprego(entity.getDescricaoOutroEmprego())
                .areaAtuacaoLat(lat)
                .areaAtuacaoLng(lng)
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
