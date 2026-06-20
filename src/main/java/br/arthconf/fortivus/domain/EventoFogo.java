package br.arthconf.fortivus.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_eventos_fogo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoFogo {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private Double latitude;
    private Double longitude;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false, length = 50)
    private String status;

    @PrePersist
    public void prePersist() {
        if (this.dataCriacao == null) {
            this.dataCriacao = LocalDateTime.now();
        }
    }
}
