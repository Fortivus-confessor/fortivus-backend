package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.BuscarRelatorioMaquinarioUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioMaquinarioUseCase;
import br.arthconf.fortivus.application.port.out.RelatorioMaquinarioPort;
import br.arthconf.fortivus.domain.Despacho;
import br.arthconf.fortivus.domain.RelatorioMaquinario;
import br.arthconf.fortivus.dto.RelatorioMaquinarioDTO;
import br.arthconf.fortivus.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RelatorioMaquinarioService implements SalvarRelatorioMaquinarioUseCase, BuscarRelatorioMaquinarioUseCase {

    private final RelatorioMaquinarioPort relatorioMaquinarioPort;
    private final DespachoRepository despachoRepository;

    @Override
    @Transactional
    public RelatorioMaquinarioDTO salvar(Long despachoId, RelatorioMaquinarioDTO dto) {
        Despacho despacho = despachoRepository.findById(despachoId)
                .orElseThrow(() -> new RuntimeException("Despacho não encontrado"));

        RelatorioMaquinario relatorio = relatorioMaquinarioPort.buscarPorDespachoId(despachoId).orElse(new RelatorioMaquinario());
        relatorio.setDespacho(despacho);
        relatorio.setId(despachoId);
        relatorio.setOperador(dto.getOperador());
        relatorio.setHorasTrabalhadas(dto.getHorasTrabalhadas());
        relatorio.setTipoMaquinario(dto.getTipoMaquinario());
        relatorio.setExtensaoLinhaDefesaMetros(dto.getExtensaoLinhaDefesaMetros());
        relatorio.setHistoricoDescritivo(dto.getHistoricoDescritivo());
        relatorio.setDataInicio(dto.getDataInicio());
        relatorio.setDataFim(dto.getDataFim());

        RelatorioMaquinario salvo = relatorioMaquinarioPort.salvar(relatorio);
        return toDTO(salvo);
    }

    @Override
    public Optional<RelatorioMaquinarioDTO> buscarPorDespachoId(Long despachoId) {
        return relatorioMaquinarioPort.buscarPorDespachoId(despachoId).map(this::toDTO);
    }

    private RelatorioMaquinarioDTO toDTO(RelatorioMaquinario rel) {
        RelatorioMaquinarioDTO dto = new RelatorioMaquinarioDTO();
        dto.setId(rel.getId());
        dto.setDespachoId(rel.getDespacho().getId());
        dto.setOperador(rel.getOperador());
        dto.setHorasTrabalhadas(rel.getHorasTrabalhadas());
        dto.setTipoMaquinario(rel.getTipoMaquinario());
        dto.setExtensaoLinhaDefesaMetros(rel.getExtensaoLinhaDefesaMetros());
        dto.setHistoricoDescritivo(rel.getHistoricoDescritivo());
        dto.setDataInicio(rel.getDataInicio());
        dto.setDataFim(rel.getDataFim());
        return dto;
    }
}
