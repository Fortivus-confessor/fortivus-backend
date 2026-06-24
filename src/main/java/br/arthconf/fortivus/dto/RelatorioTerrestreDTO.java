package br.arthconf.fortivus.dto;

import br.arthconf.fortivus.domain.AcaoCombate;
import br.arthconf.fortivus.domain.OrgaoApoio;
import br.arthconf.fortivus.domain.OrigemIncendio;
import br.arthconf.fortivus.domain.RelatorioTerrestre;
import br.arthconf.fortivus.domain.ResultadoOcorrencia;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * DTO principal para criação/edição do Relatório Terrestre via JSON.
 */
public record RelatorioTerrestreDTO(
        Long despachoId,

        // Ações realizadas
        Set<AcaoCombate> acoesRealizadas,

        // Órgãos de apoio
        Set<OrgaoApoio> orgaosApoio,
        String outrosOrgaosDescricao,

        // Área de atuação
        Double areaAtuacaoLat,
        Double areaAtuacaoLng,

        // Uso de água
        Boolean houveUsoAgua,
        Integer volumeAguaLitros,
        Set<RelatorioTerrestre.OrigemAgua> origensAgua,
        String outraOrigemAguaDescricao,

        // Apoio de propriedades
        Boolean houveApoioPropriedades,
        Boolean houveRecusaPropriedades,
        List<PropriedadeRelatorioDTO> propriedades,

        // Origem do incêndio
        OrigemIncendio possivelOrigemIncendio,
        String outraOrigemDescricao,

        // Efetividade e reforço
        RelatorioTerrestre.EfetividadeCombate efetividadeCombate,
        Boolean necessidadeReforco,
        Set<RelatorioTerrestre.TipoReforco> tiposReforcoNecessarios,

        // Histórico e resultado
        String historicoDescritivo,
        ResultadoOcorrencia resultadoOcorrencia,
        String outroResultadoDescricao,


        // Datas
        LocalDateTime dataInicio,
        LocalDateTime dataFim
) {

    /**
     * Sub-DTO para propriedades rurais (apoio ou recusa).
     */
    public record PropriedadeRelatorioDTO(
            UUID id,
            String nomePropriedade,
            String responsavel,
            String telefone,
            Double localizacaoLat,
            Double localizacaoLng,
            br.arthconf.fortivus.domain.PropriedadeRelatorio.TipoRegistro tipoRegistro,
            br.arthconf.fortivus.domain.PropriedadeRelatorio.TipoApoio tipoApoio,
            Integer quantidadeApoio,
            String descricaoApoioOutro,
            br.arthconf.fortivus.domain.PropriedadeRelatorio.MotivoRecusa motivoRecusa,
            String descricaoRecusaOutro
    ) {}
}
