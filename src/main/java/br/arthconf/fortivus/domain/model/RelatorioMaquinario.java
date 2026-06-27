package br.arthconf.fortivus.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioMaquinario {
    private Long despachoId;
    private Double horimetroInicial;
    private Double horimetroFinal;
    private String tempoLiquido;
    private LocalTime horaInicioOperacao;
    private LocalTime horaFimOperacao;
    private List<String> tiposEmprego;
    private Double comprimentoAceiros;
    private String descricaoOutroEmprego;
    private Double areaAtuacaoLat;
    private Double areaAtuacaoLng;
    private String efetividadeCombate;
    private Boolean necessidadeReforco;
    private List<String> tiposReforcoNecessarios;
    private String historicoDescritivo;
    private String resultadoOcorrencia;
    private String outroResultadoDescricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
}
