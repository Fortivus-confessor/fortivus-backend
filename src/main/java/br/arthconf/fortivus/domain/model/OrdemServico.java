package br.arthconf.fortivus.domain.model;

import br.arthconf.fortivus.domain.SituacaoOrdemServico;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdemServico {
    private Long id;
    private String descricaoTarefa;
    private UUID escalaId;
    private UUID relatorId;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataFim;
    private SituacaoOrdemServico status;
    private Long eventoFogoId;
    private String tipoDespacho;
    private UUID centroComandoId;
    private List<Despacho> despachos;

    public static OrdemServico criar(Long id, String descricaoTarefa, Long eventoFogoId, UUID escalaId, UUID responsavelId) {
        return OrdemServico.builder()
                .id(id)
                .descricaoTarefa(descricaoTarefa)
                .eventoFogoId(eventoFogoId)
                .escalaId(escalaId)
                .relatorId(responsavelId)
                .status(SituacaoOrdemServico.EM_EXECUCAO)
                .dataCriacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();
    }

    public void concluir(LocalDateTime dataFimConclusao) {
        if (this.status == SituacaoOrdemServico.CONCLUIDA) {
            throw new IllegalStateException("OS já foi concluída");
        }
        this.status = SituacaoOrdemServico.CONCLUIDA;
        this.dataFim = dataFimConclusao;
    }

    public void cancelar() {
        this.status = SituacaoOrdemServico.CANCELADA;
        this.dataFim = LocalDateTime.now();
    }
}
