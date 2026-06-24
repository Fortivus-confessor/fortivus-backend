package br.arthconf.fortivus.dto;

import br.arthconf.fortivus.domain.RelatorioMaquinario.TipoMaquinario;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RelatorioMaquinarioDTO {
    private Long id;
    private Long despachoId;
    private String operador;
    private Double horasTrabalhadas;
    private TipoMaquinario tipoMaquinario;
    private Double extensaoLinhaDefesaMetros;
    private String historicoDescritivo;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    @com.fasterxml.jackson.annotation.JsonProperty("smartId")
    public String getSmartId() {
        return despachoId != null ? "RM" + String.format("%012d", despachoId) : null;
    }
}
