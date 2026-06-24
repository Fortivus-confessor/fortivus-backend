package br.arthconf.fortivus.infrastructure.persistence.repository;

import br.arthconf.fortivus.infrastructure.persistence.entity.VeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataVeiculoRepository extends JpaRepository<VeiculoEntity, UUID> {

    @Query("SELECT v FROM VeiculoEntity v LEFT JOIN FETCH v.equipe")
    List<VeiculoEntity> findAllFetched();
}
