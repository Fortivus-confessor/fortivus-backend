package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.BuscarRelatorioMaquinarioUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioMaquinarioUseCase;
import br.arthconf.fortivus.application.port.out.RelatorioMaquinarioPort;
import br.arthconf.fortivus.domain.model.RelatorioMaquinario;
import br.arthconf.fortivus.dto.RelatorioMaquinarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RelatorioMaquinarioService implements SalvarRelatorioMaquinarioUseCase, BuscarRelatorioMaquinarioUseCase {

    private final RelatorioMaquinarioPort relatorioMaquinarioPort;

    @Override
    @Transactional
    public RelatorioMaquinarioDTO salvar(Long despachoId, RelatorioMaquinarioDTO dto) {
        RelatorioMaquinario domain = RelatorioMaquinario.builder()
                .despachoId(despachoId)
                .horimetroInicial(dto.horimetroInicial())
                .horimetroFinal(dto.horimetroFinal())
                .tempoLiquido(dto.tempoLiquido())
                .horaInicioOperacao(dto.horaInicioOperacao())
                .horaFimOperacao(dto.horaFimOperacao())
                .tiposEmprego(dto.tiposEmprego())
                .comprimentoAceiros(dto.comprimentoAceiros())
                .descricaoOutroEmprego(dto.descricaoOutroEmprego())
                .areaAtuacaoLat(dto.areaAtuacaoLat())
                .areaAtuacaoLng(dto.areaAtuacaoLng())
                .efetividadeCombate(dto.efetividadeCombate())
                .necessidadeReforco(dto.necessidadeReforco())
                .tiposReforcoNecessarios(dto.tiposReforcoNecessarios())
                .historicoDescritivo(dto.historicoDescritivo())
                .resultadoOcorrencia(dto.resultadoOcorrencia())
                .outroResultadoDescricao(dto.outroResultadoDescricao())
                .dataInicio(dto.dataInicio())
                .dataFim(dto.dataFim())
                .build();

        return toDTO(relatorioMaquinarioPort.salvar(domain));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RelatorioMaquinarioDTO> buscarPorDespachoId(Long despachoId) {
        return relatorioMaquinarioPort.buscarPorDespachoId(despachoId).map(this::toDTO);
    }

    private RelatorioMaquinarioDTO toDTO(RelatorioMaquinario rel) {
        return new RelatorioMaquinarioDTO(
                rel.getDespachoId(),
                rel.getHorimetroInicial(),
                rel.getHorimetroFinal(),
                rel.getTempoLiquido(),
                rel.getHoraInicioOperacao(),
                rel.getHoraFimOperacao(),
                rel.getTiposEmprego(),
                rel.getComprimentoAceiros(),
                rel.getDescricaoOutroEmprego(),
                rel.getAreaAtuacaoLat(),
                rel.getAreaAtuacaoLng(),
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
