package br.arthconf.fortivus.infrastructure.persistence.mapper;

import br.arthconf.fortivus.domain.model.AnexoRelatorio;
import br.arthconf.fortivus.domain.model.PropriedadeRelatorio;
import br.arthconf.fortivus.domain.model.RelatorioTerrestre;
import br.arthconf.fortivus.infrastructure.persistence.entity.AnexoRelatorioEntity;
import br.arthconf.fortivus.infrastructure.persistence.entity.PropriedadeRelatorioEntity;
import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioTerrestreEntity;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import java.util.List;
import java.util.stream.Collectors;

public class RelatorioTerrestreMapper {

    private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

    private RelatorioTerrestreMapper() {}

    public static RelatorioTerrestre toDomain(RelatorioTerrestreEntity entity) {
        Double areaLat = null, areaLng = null;
        if (entity.getAreaAtuacaoGeom() != null) {
            areaLat = entity.getAreaAtuacaoGeom().getCoordinate().y;
            areaLng = entity.getAreaAtuacaoGeom().getCoordinate().x;
        }

        List<PropriedadeRelatorio> propriedades = null;
        if (entity.getPropriedades() != null) {
            propriedades = entity.getPropriedades().stream()
                    .map(RelatorioTerrestreMapper::toPropriedadeDomain)
                    .collect(Collectors.toList());
        }

        List<AnexoRelatorio> anexos = null;
        if (entity.getAnexos() != null) {
            anexos = entity.getAnexos().stream()
                    .map(RelatorioTerrestreMapper::toAnexoDomain)
                    .collect(Collectors.toList());
        }

        return RelatorioTerrestre.builder()
                .despachoId(entity.getDespacho() != null ? entity.getDespacho().getId() : null)
                .acoesRealizadas(entity.getAcoesRealizadas())
                .orgaosApoio(entity.getOrgaosApoio())
                .outrosOrgaosDescricao(entity.getOutrosOrgaosDescricao())
                .areaAtuacaoLat(areaLat)
                .areaAtuacaoLng(areaLng)
                .houveUsoAgua(entity.getHouveUsoAgua())
                .volumeAguaLitros(entity.getVolumeAguaLitros())
                .origensAgua(entity.getOrigensAgua())
                .outraOrigemAguaDescricao(entity.getOutraOrigemAguaDescricao())
                .houveApoioPropriedades(entity.getHouveApoioPropriedades())
                .houveRecusaPropriedades(entity.getHouveRecusaPropriedades())
                .possivelOrigemIncendio(entity.getPossivelOrigemIncendio())
                .efetividadeCombate(entity.getEfetividadeCombate())
                .necessidadeReforco(entity.getNecessidadeReforco())
                .tiposReforcoNecessarios(entity.getTiposReforcoNecessarios())
                .historicoDescritivo(entity.getHistoricoDescritivo())
                .resultadoOcorrencia(entity.getResultadoOcorrencia())
                .outroResultadoDescricao(entity.getOutroResultadoDescricao())
                .propriedades(propriedades)
                .anexos(anexos)
                .dataInicio(entity.getDataInicio())
                .dataFim(entity.getDataFim())
                .build();
    }

    public static RelatorioTerrestreEntity toEntity(RelatorioTerrestre domain) {
        RelatorioTerrestreEntity entity = new RelatorioTerrestreEntity();
        entity.setAcoesRealizadas(domain.getAcoesRealizadas());
        entity.setOrgaosApoio(domain.getOrgaosApoio());
        entity.setOutrosOrgaosDescricao(domain.getOutrosOrgaosDescricao());
        entity.setHouveUsoAgua(domain.getHouveUsoAgua());
        entity.setVolumeAguaLitros(domain.getVolumeAguaLitros());
        entity.setOrigensAgua(domain.getOrigensAgua());
        entity.setOutraOrigemAguaDescricao(domain.getOutraOrigemAguaDescricao());
        entity.setHouveApoioPropriedades(domain.getHouveApoioPropriedades());
        entity.setHouveRecusaPropriedades(domain.getHouveRecusaPropriedades());
        entity.setPossivelOrigemIncendio(domain.getPossivelOrigemIncendio());
        entity.setEfetividadeCombate(domain.getEfetividadeCombate());
        entity.setNecessidadeReforco(domain.getNecessidadeReforco());
        entity.setTiposReforcoNecessarios(domain.getTiposReforcoNecessarios());
        entity.setHistoricoDescritivo(domain.getHistoricoDescritivo());
        entity.setResultadoOcorrencia(domain.getResultadoOcorrencia());
        entity.setOutroResultadoDescricao(domain.getOutroResultadoDescricao());
        entity.setDataFim(domain.getDataFim());

        if (domain.getAreaAtuacaoLat() != null && domain.getAreaAtuacaoLng() != null) {
            entity.setAreaAtuacaoGeom(
                    GF.createPoint(new Coordinate(domain.getAreaAtuacaoLng(), domain.getAreaAtuacaoLat())));
        }

        if (domain.getPropriedades() != null) {
            entity.setPropriedades(domain.getPropriedades().stream()
                    .map(RelatorioTerrestreMapper::toPropriedadeEntity)
                    .collect(Collectors.toList()));
        }

        if (domain.getAnexos() != null) {
            entity.setAnexos(domain.getAnexos().stream()
                    .map(RelatorioTerrestreMapper::toAnexoEntity)
                    .collect(Collectors.toList()));
        }

        return entity;
    }

    private static PropriedadeRelatorio toPropriedadeDomain(PropriedadeRelatorioEntity entity) {
        Double lat = null, lng = null;
        if (entity.getLocalizacaoGeom() != null) {
            lat = entity.getLocalizacaoGeom().getCoordinate().y;
            lng = entity.getLocalizacaoGeom().getCoordinate().x;
        }
        return PropriedadeRelatorio.builder()
                .id(entity.getId())
                .nomePropriedade(entity.getNomePropriedade())
                .responsavel(entity.getResponsavel())
                .telefone(entity.getTelefone())
                .localizacaoLat(lat)
                .localizacaoLng(lng)
                .tipoRegistro(entity.getTipoRegistro())
                .tipoApoio(entity.getTipoApoio())
                .quantidadeApoio(entity.getQuantidadeApoio())
                .descricaoApoioOutro(entity.getDescricaoApoioOutro())
                .motivoRecusa(entity.getMotivoRecusa())
                .descricaoRecusaOutro(entity.getDescricaoRecusaOutro())
                .build();
    }

    private static PropriedadeRelatorioEntity toPropriedadeEntity(PropriedadeRelatorio domain) {
        PropriedadeRelatorioEntity entity = new PropriedadeRelatorioEntity();
        entity.setNomePropriedade(domain.getNomePropriedade());
        entity.setResponsavel(domain.getResponsavel());
        entity.setTelefone(domain.getTelefone());
        entity.setTipoRegistro(domain.getTipoRegistro());
        entity.setTipoApoio(domain.getTipoApoio());
        entity.setQuantidadeApoio(domain.getQuantidadeApoio());
        entity.setDescricaoApoioOutro(domain.getDescricaoApoioOutro());
        entity.setMotivoRecusa(domain.getMotivoRecusa());
        entity.setDescricaoRecusaOutro(domain.getDescricaoRecusaOutro());
        if (domain.getLocalizacaoLat() != null && domain.getLocalizacaoLng() != null) {
            entity.setLocalizacaoGeom(
                    GF.createPoint(new Coordinate(domain.getLocalizacaoLng(), domain.getLocalizacaoLat())));
        }
        return entity;
    }

    private static AnexoRelatorio toAnexoDomain(AnexoRelatorioEntity entity) {
        return AnexoRelatorio.builder()
                .id(entity.getId())
                .nomeArquivo(entity.getNomeArquivo())
                .chaveS3(entity.getChaveS3())
                .contentType(entity.getContentType())
                .tamanho(entity.getTamanho())
                .dataCriacao(entity.getDataCriacao())
                .dataAtualizacao(entity.getDataAtualizacao())
                .build();
    }

    private static AnexoRelatorioEntity toAnexoEntity(AnexoRelatorio domain) {
        AnexoRelatorioEntity entity = new AnexoRelatorioEntity();
        entity.setNomeArquivo(domain.getNomeArquivo());
        entity.setChaveS3(domain.getChaveS3());
        entity.setContentType(domain.getContentType());
        entity.setTamanho(domain.getTamanho());
        return entity;
    }
}
