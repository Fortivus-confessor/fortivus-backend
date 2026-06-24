package br.arthconf.fortivus.domain.model;

import br.arthconf.fortivus.domain.SituacaoDespacho;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Despacho {
    private Long id;
    private Long ordemServicoId;
    private UUID escalaId;
    private UUID responsavelId;
    private CategoriaOperacao categoria;
    private Double latitude;
    private Double longitude;
    private String descricaoTarefa;
    private SituacaoDespacho status;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    public void finalizar(LocalDateTime dataFim) {
        if (this.status == SituacaoDespacho.CONCLUIDO) {
            throw new IllegalStateException("Despacho já finalizado");
        }
        this.status = SituacaoDespacho.CONCLUIDO;
        this.dataFim = dataFim;
    }
}
