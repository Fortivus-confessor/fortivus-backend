package br.arthconf.fortivus.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RelatorioAereoDTO(
        Long despachoId,
        Double horimetroInicial,
        Double horimetroFinal,
        String horasLiquidas,
        List<String> tiposEmprego,
        Double areaAtuacaoLat,
        Double areaAtuacaoLng,
        Integer qtdeLancamentos,
        Boolean houveUsoAgua,
        Integer volumeAguaLitros,
        List<String> origensAgua,
        String outraOrigemAguaDescricao,
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
