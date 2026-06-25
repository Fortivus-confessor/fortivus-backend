package br.arthconf.fortivus.infrastructure.persistence.entity;

import br.arthconf.fortivus.domain.EstadoOperacionalUsuario;
import br.arthconf.fortivus.domain.PerfilAcesso;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    private String primeiroNome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String cpf;

    @Column(unique = true)
    private String rg;

    @Column(unique = true)
    private String matricula;

    private String posto;

    private LocalDate dataNascimento;

    private String tipoSanguineo;

    private String fotoUrl;

    @Transient
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilAcesso perfil;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private EstadoOperacionalUsuario estadoOperacional = EstadoOperacionalUsuario.DISPONIVEL;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_comando_id")
    private CentroComandoEntity centroComando;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private EquipeEntity equipe;
}
