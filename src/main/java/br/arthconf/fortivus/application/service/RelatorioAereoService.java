package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.BuscarRelatorioAereoUseCase;
import br.arthconf.fortivus.application.port.in.SalvarRelatorioAereoUseCase;
import br.arthconf.fortivus.application.port.out.RelatorioAereoPort;
import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.domain.RelatorioAereo;
import br.arthconf.fortivus.dto.RelatorioAereoDTO;
import br.arthconf.fortivus.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RelatorioAereoService implements SalvarRelatorioAereoUseCase, BuscarRelatorioAereoUseCase {

    private final RelatorioAereoPort relatorioAereoPort;
    private final DespachoRepository despachoRepository;

    @Override
    @Transactional
    public RelatorioAereoDTO salvar(Long despachoId, RelatorioAereoDTO dto) {
        DespachoEntity DespachoEntity = despachoRepository.findById(despachoId)
                .orElseThrow(() -> new RuntimeException("DespachoEntity não encontrado"));

        RelatorioAereo relatorio = relatorioAereoPort.buscarPorDespachoId(despachoId).orElse(new RelatorioAereo());
        relatorio.setDespacho(DespachoEntity);
        relatorio.setId(despachoId);
        relatorio.setHorimetroInicial(dto.horimetroInicial());
        relatorio.setHorimetroFinal(dto.horimetroFinal());
        relatorio.setHorasLiquidas(dto.horasLiquidas());
        relatorio.setTiposEmprego(dto.tiposEmprego());
        relatorio.setQtdeLancamentos(dto.qtdeLancamentos());
        relatorio.setHouveUsoAgua(dto.houveUsoAgua());
        relatorio.setVolumeAguaLitros(dto.volumeAguaLitros());
        relatorio.setOrigensAgua(dto.origensAgua());
        relatorio.setOutraOrigemAguaDescricao(dto.outraOrigemAguaDescricao());
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
            org.locationtech.jts.geom.GeometryFactory gf = new org.locationtech.jts.geom.GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);
            relatorio.setAreaAtuacaoGeom(gf.createPoint(new org.locationtech.jts.geom.Coordinate(dto.areaAtuacaoLng(), dto.areaAtuacaoLat())));
        }

        RelatorioAereo salvo = relatorioAereoPort.salvar(relatorio);
        return toDTO(salvo);
    }

    @Override
    public Optional<RelatorioAereoDTO> buscarPorDespachoId(Long despachoId) {
        return relatorioAereoPort.buscarPorDespachoId(despachoId).map(this::toDTO);
    }

    private RelatorioAereoDTO toDTO(RelatorioAereo rel) {
        Double lat = null, lng = null;
        if (rel.getAreaAtuacaoGeom() != null) {
            lat = rel.getAreaAtuacaoGeom().getCoordinate().y;
            lng = rel.getAreaAtuacaoGeom().getCoordinate().x;
        }

        return new RelatorioAereoDTO(
            rel.getDespacho().getId(),
            rel.getHorimetroInicial(),
            rel.getHorimetroFinal(),
            rel.getHorasLiquidas(),
            rel.getTiposEmprego(),
            lat,
            lng,
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

