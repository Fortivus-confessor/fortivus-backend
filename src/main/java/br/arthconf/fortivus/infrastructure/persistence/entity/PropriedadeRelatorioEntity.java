package br.arthconf.fortivus.infrastructure.persistence.entity;

import br.arthconf.fortivus.domain.BaseEntity;
import br.arthconf.fortivus.domain.MotivoRecusa;
import br.arthconf.fortivus.domain.TipoApoio;
import br.arthconf.fortivus.domain.TipoRegistro;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.locationtech.jts.geom.Geometry;

@Entity
@Table(name = "relatorio_propriedades")
@Data
@EqualsAndHashCode(callSuper = true)
public class PropriedadeRelatorioEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioTerrestreEntity relatorio;

    @Column(name = "nome_propriedade")
    private String nomePropriedade;

    private String responsavel;
    private String telefone;

    @Column(name = "localizacao_geom", columnDefinition = "geometry(Point, 4326)")
    private Geometry localizacaoGeom;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_registro")
    private TipoRegistro tipoRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_apoio")
    private TipoApoio tipoApoio;

    @Column(name = "quantidade_apoio")
    private Integer quantidadeApoio;

    @Column(name = "descricao_apoio_outro")
    private String descricaoApoioOutro;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_recusa")
    private MotivoRecusa motivoRecusa;

    @Column(name = "descricao_recusa_outro")
    private String descricaoRecusaOutro;
}
