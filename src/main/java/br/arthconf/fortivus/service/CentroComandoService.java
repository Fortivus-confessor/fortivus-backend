package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.CentroComando;
import br.arthconf.fortivus.repository.CentroComandoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CentroComandoService {

    private final CentroComandoRepository centroComandoRepository;

    @Transactional(readOnly = true)
    public List<CentroComando> listarTodos() {
        var lista = centroComandoRepository.findAllOrdered();
        return lista != null ? lista : new ArrayList<>();
    }

    @Transactional
    public CentroComando salvar(CentroComando centroComando) {
        return centroComandoRepository.save(centroComando);
    }

    @Transactional(readOnly = true)
    public CentroComando buscarPorId(UUID id) {
        return centroComandoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Centro de Comando não encontrado"));
    }

    @Transactional
    public void deletar(UUID id) {
        centroComandoRepository.deleteById(id);
    }
}
