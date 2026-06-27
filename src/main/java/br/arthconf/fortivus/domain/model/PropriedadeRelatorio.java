package br.arthconf.fortivus.domain.model;

import br.arthconf.fortivus.domain.MotivoRecusa;
import br.arthconf.fortivus.domain.TipoApoio;
import br.arthconf.fortivus.domain.TipoRegistro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropriedadeRelatorio {
    private UUID id;
    private String nomePropriedade;
    private String responsavel;
    private String telefone;
    private Double localizacaoLat;
    private Double localizacaoLng;
    private TipoRegistro tipoRegistro;
    private TipoApoio tipoApoio;
    private Integer quantidadeApoio;
    private String descricaoApoioOutro;
    private MotivoRecusa motivoRecusa;
    private String descricaoRecusaOutro;
}
