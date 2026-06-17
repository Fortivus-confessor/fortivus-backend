package br.arthconf.fortivus.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_focos_incendio")
public class FocoIncendio extends BaseEntity {

    @Column(name = "codigo_inpe", unique = true)
    private String codigoInpe; // Opcional, usado para focos de satélite

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String bioma;

    private String municipio;

    private String estado;

    @Column(name = "satelite_referencia")
    private String sateliteReferencia;

    @Column(name = "risco_fogo")
    private String riscoFogo; // BAIXO, MEDIO, ALTO, CRITICO

    @Column(name = "frp")
    private Double frp; // Fire Radiative Power (MW)

    @Column(name = "area_estimada_hectares")
    private Double areaEstimadaHectares;

    @Column(name = "data_hora_deteccao", nullable = false)
    private LocalDateTime dataHoraDeteccao;

    @Column(name = "origem_registro", nullable = false)
    private String origemRegistro; // MANUAL, INPE, NASA, MOBILE

    @Column(name = "status", nullable = false)
    private String status; // ATIVO, CONTROLADO, EXTINTO, FALSO_ALARME

    @Column(name = "cadastrado_por_id")
    private String cadastradoPorId; // ID do usuário se for manual
}
