package br.arthconf.fortivus.dto;

import br.arthconf.fortivus.domain.RelatorioAereo.TipoAtuacaoAerea;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RelatorioAereoDTO {
    private Long id;
    private Long despachoId;
    private String aeronavePrefixo;
    private String pilotoComandante;
    private Double tempoVooHoras;
    private Integer volumeAguaLancado;
    private Integer qtdeLancamentos;
    private TipoAtuacaoAerea tipoAtuacao;
    private String historicoDescritivo;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    @com.fasterxml.jackson.annotation.JsonProperty("smartId")
    public String getSmartId() {
        return despachoId != null ? "RA" + String.format("%012d", despachoId) : null;
    }
}
