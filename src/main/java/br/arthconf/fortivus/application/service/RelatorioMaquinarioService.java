package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.BuscarRelatorioMaquinarioUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioMaquinarioUseCase;
import br.arthconf.fortivus.application.port.out.RelatorioMaquinarioPort;
import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.infrastructure.persistence.entity.RelatorioMaquinarioEntity;
import br.arthconf.fortivus.dto.RelatorioMaquinarioDTO;
import br.arthconf.fortivus.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RelatorioMaquinarioService implements SalvarRelatorioMaquinarioUseCase, BuscarRelatorioMaquinarioUseCase {

    private final RelatorioMaquinarioPort relatorioMaquinarioPort;
    private final DespachoRepository despachoRepository;

    @Override
    @Transactional
    public RelatorioMaquinarioDTO salvar(Long despachoId, RelatorioMaquinarioDTO dto) {
        DespachoEntity despacho = despachoRepository.findById(despachoId)
                .orElseThrow(() -> new RuntimeException("DespachoEntity não encontrado"));

        RelatorioMaquinarioEntity relatorio = relatorioMaquinarioPort.buscarPorDespachoId(despachoId)
                .orElse(new RelatorioMaquinarioEntity());
        relatorio.setDespacho(despacho);
        relatorio.setId(despachoId);
        relatorio.setHorimetroInicial(dto.horimetroInicial());
        relatorio.setHorimetroFinal(dto.horimetroFinal());
        relatorio.setTempoLiquido(dto.tempoLiquido());
        relatorio.setHoraInicioOperacao(dto.horaInicioOperacao());
        relatorio.setHoraFimOperacao(dto.horaFimOperacao());
        relatorio.setTiposEmprego(dto.tiposEmprego());
        relatorio.setComprimentoAceiros(dto.comprimentoAceiros());
        relatorio.setDescricaoOutroEmprego(dto.descricaoOutroEmprego());
        relatorio.setEfetividadeCombate(dto.efetividadeCombate());
        relatorio.setNecessidadeReforco(dto.necessidadeReforco());
        relatorio.setTiposReforcoNecessarios(dto.tiposReforcoNecessarios());
        relatorio.setHistoricoDescritivo(dto.historicoDescritivo());
        relatorio.setResultadoOcorrencia(dto.resultadoOcorrencia());
        relatorio.setOutroResultadoDescricao(dto.outroResultadoDescricao());

        if (dto.dataInicio() != null) {
            relatorio.setDataInicio(dto.dataInicio());
        } else if (relatorio.getDataInicio() == null) {
            relatorio.setDataInicio(LocalDateTime.now());
        }
        relatorio.setDataFim(dto.dataFim() != null ? dto.dataFim() : LocalDateTime.now());

        if (dto.areaAtuacaoLat() != null && dto.areaAtuacaoLng() != null) {
            GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
            relatorio.setAreaAtuacaoGeom(gf.createPoint(new Coordinate(dto.areaAtuacaoLng(), dto.areaAtuacaoLat())));
        }

        RelatorioMaquinarioEntity salvo = relatorioMaquinarioPort.salvar(relatorio);
        return toDTO(salvo);
    }

    @Override
    public Optional<RelatorioMaquinarioDTO> buscarPorDespachoId(Long despachoId) {
        return relatorioMaquinarioPort.buscarPorDespachoId(despachoId).map(this::toDTO);
    }

    private RelatorioMaquinarioDTO toDTO(RelatorioMaquinarioEntity rel) {
        Double lat = null, lng = null;
        if (rel.getAreaAtuacaoGeom() != null) {
            lat = rel.getAreaAtuacaoGeom().getCoordinate().y;
            lng = rel.getAreaAtuacaoGeom().getCoordinate().x;
        }
        return new RelatorioMaquinarioDTO(
            rel.getDespacho().getId(),
            rel.getHorimetroInicial(),
            rel.getHorimetroFinal(),
            rel.getTempoLiquido(),
            rel.getHoraInicioOperacao(),
            rel.getHoraFimOperacao(),
            rel.getTiposEmprego(),
            rel.getComprimentoAceiros(),
            rel.getDescricaoOutroEmprego(),
            lat,
            lng,
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
