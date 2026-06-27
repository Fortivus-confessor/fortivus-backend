package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.BuscarRelatorioAereoUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioAereoUseCase;
import br.arthconf.fortivus.application.port.out.RelatorioAereoPort;
import br.arthconf.fortivus.domain.model.RelatorioAereo;
import br.arthconf.fortivus.dto.RelatorioAereoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RelatorioAereoService implements SalvarRelatorioAereoUseCase, BuscarRelatorioAereoUseCase {

    private final RelatorioAereoPort relatorioAereoPort;

    @Override
    @Transactional
    public RelatorioAereoDTO salvar(Long despachoId, RelatorioAereoDTO dto) {
        RelatorioAereo domain = RelatorioAereo.builder()
                .despachoId(despachoId)
                .horimetroInicial(dto.horimetroInicial())
                .horimetroFinal(dto.horimetroFinal())
                .horasLiquidas(dto.horasLiquidas())
                .tiposEmprego(dto.tiposEmprego())
                .areaAtuacaoLat(dto.areaAtuacaoLat())
                .areaAtuacaoLng(dto.areaAtuacaoLng())
                .qtdeLancamentos(dto.qtdeLancamentos())
                .houveUsoAgua(dto.houveUsoAgua())
                .volumeAguaLitros(dto.volumeAguaLitros())
                .origensAgua(dto.origensAgua())
                .outraOrigemAguaDescricao(dto.outraOrigemAguaDescricao())
                .efetividadeCombate(dto.efetividadeCombate())
                .necessidadeReforco(dto.necessidadeReforco())
                .tiposReforcoNecessarios(dto.tiposReforcoNecessarios())
                .historicoDescritivo(dto.historicoDescritivo())
                .resultadoOcorrencia(dto.resultadoOcorrencia())
                .outroResultadoDescricao(dto.outroResultadoDescricao())
                .dataInicio(dto.dataInicio())
                .dataFim(dto.dataFim())
                .build();

        return toDTO(relatorioAereoPort.salvar(domain));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RelatorioAereoDTO> buscarPorDespachoId(Long despachoId) {
        return relatorioAereoPort.buscarPorDespachoId(despachoId).map(this::toDTO);
    }

    private RelatorioAereoDTO toDTO(RelatorioAereo rel) {
        return new RelatorioAereoDTO(
                rel.getDespachoId(),
                rel.getHorimetroInicial(),
                rel.getHorimetroFinal(),
                rel.getHorasLiquidas(),
                rel.getTiposEmprego(),
                rel.getAreaAtuacaoLat(),
                rel.getAreaAtuacaoLng(),
                rel.getQtdeLancamentos(),
                rel.getHouveUsoAgua(),
                rel.getVolumeAguaLitros(),
                rel.getOrigensAgua(),
                rel.getOutraOrigemAguaDescricao(),
                rel.getEfetividadeCombate(),
                rel.getNecessidadeReforco(),
                rel.getTiposReforcoNecessarios(),
                rel.getHistoricoDescritivo(),
                rel.getResultadoOcorrencia(),
                rel.getOutroResultadoDescricao(),
                rel.getDataInicio(),
                rel.getDataFim()
        );
    }
}
