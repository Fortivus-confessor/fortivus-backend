package br.arthconf.fortivus.domain.model;

import br.arthconf.fortivus.domain.AcaoCombate;
import br.arthconf.fortivus.domain.EfetividadeCombate;
import br.arthconf.fortivus.domain.OrgaoApoio;
import br.arthconf.fortivus.domain.OrigemAgua;
import br.arthconf.fortivus.domain.OrigemIncendio;
import br.arthconf.fortivus.domain.ResultadoOcorrencia;
import br.arthconf.fortivus.domain.TipoReforco;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioTerrestre {
    private Long despachoId;
    private Set<AcaoCombate> acoesRealizadas;
    private Set<OrgaoApoio> orgaosApoio;
    private String outrosOrgaosDescricao;
    private Double areaAtuacaoLat;
    private Double areaAtuacaoLng;
    private Boolean houveUsoAgua;
    private Integer volumeAguaLitros;
    private Set<OrigemAgua> origensAgua;
    private String outraOrigemAguaDescricao;
    private Boolean houveApoioPropriedades;
    private Boolean houveRecusaPropriedades;
    private OrigemIncendio possivelOrigemIncendio;
    private EfetividadeCombate efetividadeCombate;
    private Boolean necessidadeReforco;
    private Set<TipoReforco> tiposReforcoNecessarios;
    private String historicoDescritivo;
    private ResultadoOcorrencia resultadoOcorrencia;
    private String outroResultadoDescricao;
    private List<PropriedadeRelatorio> propriedades;
    private List<AnexoRelatorio> anexos;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
}
