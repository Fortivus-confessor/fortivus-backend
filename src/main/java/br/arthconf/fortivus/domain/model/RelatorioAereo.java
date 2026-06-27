package br.arthconf.fortivus.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioAereo {
    private Long despachoId;
    private Double horimetroInicial;
    private Double horimetroFinal;
    private String horasLiquidas;
    private List<String> tiposEmprego;
    private Double areaAtuacaoLat;
    private Double areaAtuacaoLng;
    private Integer qtdeLancamentos;
    private Boolean houveUsoAgua;
    private Integer volumeAguaLitros;
    private List<String> origensAgua;
    private String outraOrigemAguaDescricao;
    private String efetividadeCombate;
    private Boolean necessidadeReforco;
    private List<String> tiposReforcoNecessarios;
    private String historicoDescritivo;
    private String resultadoOcorrencia;
    private String outroResultadoDescricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
}
