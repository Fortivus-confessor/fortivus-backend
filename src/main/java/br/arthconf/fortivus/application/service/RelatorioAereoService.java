package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.BuscarRelatorioAereoUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioAereoUseCase;
import br.arthconf.fortivus.application.port.out.RelatorioAereoPort;
import br.arthconf.fortivus.domain.Despacho;
import br.arthconf.fortivus.domain.RelatorioAereo;
import br.arthconf.fortivus.dto.RelatorioAereoDTO;
import br.arthconf.fortivus.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RelatorioAereoService implements SalvarRelatorioAereoUseCase, BuscarRelatorioAereoUseCase {

    private final RelatorioAereoPort relatorioAereoPort;
    private final DespachoRepository despachoRepository;

    @Override
    @Transactional
    public RelatorioAereoDTO salvar(Long despachoId, RelatorioAereoDTO dto) {
        Despacho despacho = despachoRepository.findById(despachoId)
                .orElseThrow(() -> new RuntimeException("Despacho não encontrado"));

        RelatorioAereo relatorio = relatorioAereoPort.buscarPorDespachoId(despachoId).orElse(new RelatorioAereo());
        relatorio.setDespacho(despacho);
        relatorio.setId(despachoId);
        relatorio.setAeronavePrefixo(dto.getAeronavePrefixo());
        relatorio.setPilotoComandante(dto.getPilotoComandante());
        relatorio.setTempoVooHoras(dto.getTempoVooHoras());
        relatorio.setVolumeAguaLancado(dto.getVolumeAguaLancado());
        relatorio.setQtdeLancamentos(dto.getQtdeLancamentos());
        relatorio.setTipoAtuacao(dto.getTipoAtuacao());
        relatorio.setHistoricoDescritivo(dto.getHistoricoDescritivo());
        relatorio.setDataInicio(dto.getDataInicio());
        relatorio.setDataFim(dto.getDataFim());

        RelatorioAereo salvo = relatorioAereoPort.salvar(relatorio);
        return toDTO(salvo);
    }

    @Override
    public Optional<RelatorioAereoDTO> buscarPorDespachoId(Long despachoId) {
        return relatorioAereoPort.buscarPorDespachoId(despachoId).map(this::toDTO);
    }

    private RelatorioAereoDTO toDTO(RelatorioAereo rel) {
        RelatorioAereoDTO dto = new RelatorioAereoDTO();
        dto.setId(rel.getId());
        dto.setDespachoId(rel.getDespacho().getId());
        dto.setAeronavePrefixo(rel.getAeronavePrefixo());
        dto.setPilotoComandante(rel.getPilotoComandante());
        dto.setTempoVooHoras(rel.getTempoVooHoras());
        dto.setVolumeAguaLancado(rel.getVolumeAguaLancado());
        dto.setQtdeLancamentos(rel.getQtdeLancamentos());
        dto.setTipoAtuacao(rel.getTipoAtuacao());
        dto.setHistoricoDescritivo(rel.getHistoricoDescritivo());
        dto.setDataInicio(rel.getDataInicio());
        dto.setDataFim(rel.getDataFim());
        return dto;
    }
}
