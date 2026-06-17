package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.Equipamento;
import br.arthconf.fortivus.repository.EquipamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;

    @Transactional(readOnly = true)
    public List<Equipamento> listarTodos() {
        var lista = equipamentoRepository.findAllWithEquipe();
        return lista != null ? lista : new ArrayList<>();
    }

    @Transactional
    public Equipamento salvar(Equipamento equipamento) {
        return equipamentoRepository.save(equipamento);
    }

    @Transactional(readOnly = true)
    public Equipamento buscarPorId(UUID id) {
        return equipamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));
    }

    @Transactional
    public void deletar(UUID id) {
        equipamentoRepository.deleteById(id);
    }
}
