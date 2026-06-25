package br.arthconf.fortivus.dto;

import br.arthconf.fortivus.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record RelatorioTerrestreDTO(
        Long despachoId,

        Set<AcaoCombate> acoesRealizadas,

        Set<OrgaoApoio> orgaosApoio,
        String outrosOrgaosDescricao,

        Double areaAtuacaoLat,
        Double areaAtuacaoLng,

        Boolean houveUsoAgua,
        Integer volumeAguaLitros,
        Set<OrigemAgua> origensAgua,
        String outraOrigemAguaDescricao,

        Boolean houveApoioPropriedades,
        Boolean houveRecusaPropriedades,
        List<PropriedadeRelatorioDTO> propriedades,

        OrigemIncendio possivelOrigemIncendio,
        String outraOrigemDescricao,

        EfetividadeCombate efetividadeCombate,
        Boolean necessidadeReforco,
        Set<TipoReforco> tiposReforcoNecessarios,

        String historicoDescritivo,
        ResultadoOcorrencia resultadoOcorrencia,
        String outroResultadoDescricao,

        LocalDateTime dataInicio,
        LocalDateTime dataFim
) {
    @com.fasterxml.jackson.annotation.JsonProperty("smartId")
    public String getSmartId() {
        return despachoId != null ? "RT" + String.format("%012d", despachoId) : null;
    }

    public record PropriedadeRelatorioDTO(
            UUID id,
            String nomePropriedade,
            String responsavel,
            String telefone,
            Double localizacaoLat,
            Double localizacaoLng,
            TipoRegistro tipoRegistro,
            TipoApoio tipoApoio,
            Integer quantidadeApoio,
            String descricaoApoioOutro,
            MotivoRecusa motivoRecusa,
            String descricaoRecusaOutro
    ) {}
}
