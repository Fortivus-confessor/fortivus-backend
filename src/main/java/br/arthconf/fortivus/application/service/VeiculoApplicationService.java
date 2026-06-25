package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.GerenciarVeiculoUseCase;
import br.arthconf.fortivus.application.port.in.ListarVeiculosUseCase;
import br.arthconf.fortivus.application.port.out.VeiculoPort;
import br.arthconf.fortivus.domain.model.Veiculo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VeiculoApplicationService implements ListarVeiculosUseCase, GerenciarVeiculoUseCase {

    private final VeiculoPort veiculoPort;

    @Override
    @Transactional(readOnly = true)
    public List<Veiculo> listarTodos() {
        var lista = veiculoPort.findAllFetched();
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Override
    @Transactional
    public Veiculo salvar(Veiculo veiculo) {
        return veiculoPort.save(veiculo);
    }

    @Override
    @Transactional(readOnly = true)
    public Veiculo buscarPorId(UUID id) {
        return veiculoPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
    }

    @Override
    @Transactional
    public void deletar(UUID id) {
        veiculoPort.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Veiculo> listarPaginado(Pageable pageable) {
        return veiculoPort.findAll(pageable);
    }
}
