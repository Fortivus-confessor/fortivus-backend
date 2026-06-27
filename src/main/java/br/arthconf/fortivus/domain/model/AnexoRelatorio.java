package br.arthconf.fortivus.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnexoRelatorio {
    private UUID id;
    private String nomeArquivo;
    private String chaveS3;
    private String contentType;
    private Long tamanho;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
