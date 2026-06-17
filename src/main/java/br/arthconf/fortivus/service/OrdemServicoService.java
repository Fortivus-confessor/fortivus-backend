package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.OrdemServico;
import br.arthconf.fortivus.domain.Despacho;
import br.arthconf.fortivus.domain.SituacaoOrdemServico;
import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.repository.OrdemServicoRepository;
import br.arthconf.fortivus.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final OrdemServicoRepository osRepository;
    private final DespachoRepository despachoRepository;

    @Transactional(readOnly = true)
    public List<OrdemServico> listarTodas() {
        var lista = osRepository.findAllFetched();
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Transactional
    public OrdemServico criarOS(OrdemServico os) {
        if (os.getId() == null) {
            os.setId(System.currentTimeMillis());
            os.setDataCriacao(LocalDateTime.now());
            os.setStatus(SituacaoOrdemServico.ABERTA);
        }
        
        OrdemServico salva = osRepository.save(os);

        // Cria Despacho Inicial se for nova OS
        if (salva.getDespachos().isEmpty()) {
            Despacho despacho = new Despacho();
            despacho.setId(System.currentTimeMillis() + 1); // Diferencia do ID da OS
            despacho.setOrdemServico(salva);
            despacho.setEscala(salva.getEscala()); // Vincula a escala inicial
            despacho.setStatus(SituacaoDespacho.EM_ANDAMENTO);
            despacho.setDataInicio(LocalDateTime.now());
            // O primeiro despacho herda a localização inicial informada na OS
            despacho.setLocalizacaoGeom(salva.getLocalizacaoGeom());
            despachoRepository.save(despacho);
        }

        return salva;
    }

    @Transactional
    public void deletar(Long id) {
        osRepository.deleteById(id);
    }
    @Transactional(readOnly = true)
    public OrdemServico buscarPorId(Long id) {
        OrdemServico os = osRepository.findByIdFetched(id)
                .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada"));
        os.getDespachos().size(); // Touch para inicializar coleção LAZY
        return os;
    }
}
