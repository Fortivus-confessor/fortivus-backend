package br.arthconf.fortivus.infrastructure.persistence.repository;

import br.arthconf.fortivus.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioEntity, UUID> {
    
    Optional<UsuarioEntity> findByEmailIgnoreCase(String email);

    Optional<UsuarioEntity> findByCpf(String cpf);

    @Query("SELECT u FROM UsuarioEntity u LEFT JOIN FETCH u.centroComando LEFT JOIN FETCH u.equipe")
    List<UsuarioEntity> findAllFetched();

    @Query("SELECT u FROM UsuarioEntity u LEFT JOIN FETCH u.centroComando LEFT JOIN FETCH u.equipe WHERE u.centroComando.id = :centroId")
    List<UsuarioEntity> findByCentroComandoId(@Param("centroId") UUID centroId);
    
    @Query("SELECT u FROM UsuarioEntity u LEFT JOIN FETCH u.centroComando LEFT JOIN FETCH u.equipe WHERE u.id = :id")
    Optional<UsuarioEntity> findByIdFetched(@Param("id") UUID id);
}
