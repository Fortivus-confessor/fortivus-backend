package br.arthconf.fortivus.infrastructure.persistence.entity;

import br.arthconf.fortivus.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "relatorio_anexos")
@Data
@EqualsAndHashCode(callSuper = true)
public class AnexoRelatorioEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioTerrestreEntity relatorio;

    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    @Column(name = "chave_s3")
    private String chaveS3;

    @Column(name = "content_type")
    private String contentType;

    private Long tamanho;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;
}
