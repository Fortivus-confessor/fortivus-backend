package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    
    Optional<Usuario> findByEmailIgnoreCase(String email);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.centroComando LEFT JOIN FETCH u.equipe")
    List<Usuario> findAllFetched();

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.centroComando LEFT JOIN FETCH u.equipe WHERE u.centroComando.id = :centroId")
    List<Usuario> findByCentroComandoId(@Param("centroId") UUID centroId);
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.centroComando LEFT JOIN FETCH u.equipe WHERE u.id = :id")
    Optional<Usuario> findByIdFetched(@Param("id") UUID id);
}
