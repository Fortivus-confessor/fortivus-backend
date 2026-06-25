package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.AtualizarDespachoUseCase;
import br.arthconf.fortivus.application.port.out.DespachoRepositoryPort;
import br.arthconf.fortivus.domain.model.Despacho;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtualizarDespachoService implements AtualizarDespachoUseCase {

    private final DespachoRepositoryPort despachoPort;

    @Override
    public Despacho executar(Long id, Despacho atualizado) {
        Despacho existente = despachoPort.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Despacho não encontrado"));
        
        if (atualizado.getOrdemServicoId() != null) existente.setOrdemServicoId(atualizado.getOrdemServicoId());
        if (atualizado.getEscalaId() != null) existente.setEscalaId(atualizado.getEscalaId());
        if (atualizado.getResponsavelId() != null) existente.setResponsavelId(atualizado.getResponsavelId());
        if (atualizado.getCategoria() != null) existente.setCategoria(atualizado.getCategoria());
        if (atualizado.getLatitude() != null) existente.setLatitude(atualizado.getLatitude());
        if (atualizado.getLongitude() != null) existente.setLongitude(atualizado.getLongitude());
        if (atualizado.getDescricaoTarefa() != null) existente.setDescricaoTarefa(atualizado.getDescricaoTarefa());
        if (atualizado.getStatus() != null) existente.setStatus(atualizado.getStatus());
        if (atualizado.getDataInicio() != null) existente.setDataInicio(atualizado.getDataInicio());
        if (atualizado.getDataFim() != null) existente.setDataFim(atualizado.getDataFim());

        return despachoPort.salvar(existente);
    }
}
