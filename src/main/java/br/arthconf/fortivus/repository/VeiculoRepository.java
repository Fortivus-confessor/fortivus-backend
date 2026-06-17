package br.arthconf.fortivus.repository;

import br.arthconf.fortivus.domain.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, UUID> {

    @Query("SELECT v FROM Veiculo v LEFT JOIN FETCH v.equipe")
    List<Veiculo> findAllFetched();
}
