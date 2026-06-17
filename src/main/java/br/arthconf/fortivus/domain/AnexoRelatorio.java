package br.arthconf.fortivus.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "relatorio_anexos")
@Data
@EqualsAndHashCode(callSuper = true)
public class AnexoRelatorio extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioTerrestre relatorio;

    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    @Column(name = "chave_s3")
    private String chaveS3;

    @Column(name = "content_type")
    private String contentType;

    private Long tamanho;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private java.time.LocalDateTime dataCriacao;

    @org.hibernate.annotations.UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private java.time.LocalDateTime dataAtualizacao;
}
