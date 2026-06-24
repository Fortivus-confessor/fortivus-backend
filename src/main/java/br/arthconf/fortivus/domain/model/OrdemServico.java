package br.arthconf.fortivus.domain.model;

import br.arthconf.fortivus.domain.SituacaoOrdemServico;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class OrdemServico {
    private Long id;
    private String descricaoTarefa;
    private UUID escalaId;
    private UUID relatorId;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataFim;
    private SituacaoOrdemServico status;
    private Long eventoFogoId;

    public void atualizarStatus(SituacaoOrdemServico novoStatus) {
        this.status = novoStatus;
        if (novoStatus == SituacaoOrdemServico.CONCLUIDA || novoStatus == SituacaoOrdemServico.CANCELADA) {
            this.dataFim = LocalDateTime.now();
        }
    }
}
