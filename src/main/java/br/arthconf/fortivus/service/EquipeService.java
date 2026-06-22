package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.Equipe;
import br.arthconf.fortivus.repository.EquipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class EquipeService {

    private final EquipeRepository equipeRepository;

    @Transactional(readOnly = true)
    public List<Equipe> listarTodas() {
        // Busca com JOIN FETCH já definido no Repository
        List<Equipe> lista = equipeRepository.findAllComCentro();
        // Converte para ArrayList pura para garantir que o Thymeleaf 
        // não tente disparar Lazy Loading fora da transação.
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<Equipe> buscarPorCentro(UUID centroId) {
        var lista = equipeRepository.findByCentroComandoId(centroId);
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Transactional
    public Equipe salvar(Equipe equipe) {
        return equipeRepository.save(equipe);
    }

    @Transactional(readOnly = true)
    public Equipe buscarPorId(UUID id) {
        return equipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipe não encontrada"));
    }

    @Transactional
    public void deletar(UUID id) {
        equipeRepository.deleteById(id);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Equipe> listarPaginado(org.springframework.data.domain.Pageable pageable) {
        return equipeRepository.findAll(pageable);
    }
}
