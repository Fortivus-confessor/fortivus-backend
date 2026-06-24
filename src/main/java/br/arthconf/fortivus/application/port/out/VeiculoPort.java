package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.domain.model.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VeiculoPort {
    List<Veiculo> findAllFetched();
    Veiculo save(Veiculo veiculo);
    Optional<Veiculo> findById(UUID id);
    void deleteById(UUID id);
    Page<Veiculo> findAll(Pageable pageable);
}
