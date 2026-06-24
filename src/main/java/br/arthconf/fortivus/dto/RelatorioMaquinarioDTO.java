package br.arthconf.fortivus.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record RelatorioMaquinarioDTO(
        Long despachoId,
        Double horimetroInicial,
        Double horimetroFinal,
        String tempoLiquido,
        LocalTime horaInicioOperacao,
        LocalTime horaFimOperacao,
        List<String> tiposEmprego,
        Double comprimentoAceiros,
        String descricaoOutroEmprego,
        Double areaAtuacaoLat,
        Double areaAtuacaoLng,
        String efetividadeCombate,
        Boolean necessidadeReforco,
        List<String> tiposReforcoNecessarios,
        String historicoDescritivo,
        String resultadoOcorrencia,
        String outroResultadoDescricao,
        LocalDateTime dataInicio,
        LocalDateTime dataFim
) {
}
