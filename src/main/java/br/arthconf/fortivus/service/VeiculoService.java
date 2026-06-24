package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.model.Veiculo;
import br.arthconf.fortivus.application.port.out.VeiculoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoPort veiculoPort;

    @Transactional(readOnly = true)
    public List<Veiculo> listarTodos() {
        var lista = veiculoPort.findAllFetched();
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Transactional
    public Veiculo salvar(Veiculo veiculo) {
        return veiculoPort.save(veiculo);
    }

    @Transactional(readOnly = true)
    public Veiculo buscarPorId(UUID id) {
        return veiculoPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
    }

    @Transactional
    public void deletar(UUID id) {
        veiculoPort.deleteById(id);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Veiculo> listarPaginado(org.springframework.data.domain.Pageable pageable) {
        return veiculoPort.findAll(pageable);
    }
}
