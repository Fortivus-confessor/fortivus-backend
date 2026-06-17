package br.arthconf.fortivus.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FocoIncendioDTO {
    private UUID id;
    private String codigoInpe;
    private Double latitude;
    private Double longitude;
    private String bioma;
    private String municipio;
    private String estado;
    private String sateliteReferencia;
    private String riscoFogo;
    private Double frp;
    private Double areaEstimadaHectares;
    private LocalDateTime dataHoraDeteccao;
    private String origemRegistro;
    private String status;
    private String cadastradoPorId;
}
