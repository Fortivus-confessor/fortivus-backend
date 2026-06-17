package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.Despacho;
import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.repository.DespachoRepository;
import br.arthconf.fortivus.repository.RelatorioTerrestreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DespachoService {

    private final DespachoRepository despachoRepository;
    private final RelatorioTerrestreRepository relatorioTerrestreRepository;

    @Transactional(readOnly = true)
    public List<Despacho> listarTodos() {
        var lista = despachoRepository.findAllWithDetails();
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Transactional
    public void atualizarStatus(Long id, SituacaoDespacho novoStatus) {
        var despacho = despachoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despacho não encontrado"));
        despacho.setStatus(novoStatus);
        if (novoStatus == SituacaoDespacho.CONCLUIDO) {
            despacho.setDataFim(LocalDateTime.now());
        }
        despachoRepository.save(despacho);
    }

    @Transactional
    public Despacho salvar(Despacho despacho) {
        if (despacho.getId() == null) {
            despacho.setId(System.currentTimeMillis());
        }
        return despachoRepository.save(despacho);
    }

    @Transactional
    public void deletar(Long id) {
        if (relatorioTerrestreRepository.existsById(id)) {
            relatorioTerrestreRepository.deleteById(id);
        }
        despachoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Despacho buscarPorId(Long id) {
        return despachoRepository.findByIdFetched(id).orElse(null);
    }
}
