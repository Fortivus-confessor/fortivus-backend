package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.CriarOrdemServicoUseCase;
import br.arthconf.fortivus.application.port.out.DespachoRepositoryPort;
import br.arthconf.fortivus.application.port.out.OrdemServicoRepositoryPort;
import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.domain.model.Despacho;
import br.arthconf.fortivus.domain.model.OrdemServico;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class CriarOrdemServicoService implements CriarOrdemServicoUseCase {

    private final OrdemServicoRepositoryPort osPort;
    private final DespachoRepositoryPort despachoPort;

    @Override
    @Transactional
    public OrdemServico executar(Command cmd) {
        long anoAtual = LocalDateTime.now().getYear();
        long minId = anoAtual * 100000000L;
        long maxId = (anoAtual + 1) * 100000000L;

        Long maxOsId = osPort.findMaxId(minId, maxId).orElse(minId);
        Long osId = maxOsId.equals(minId) ? minId + 1 : maxOsId + 1;

        OrdemServico os = OrdemServico.criar(osId, cmd.descricaoTarefa(), cmd.eventoFogoId(), cmd.escalaId(), cmd.responsavelId());
        os = osPort.salvar(os);

        Despacho despacho = Despacho.builder()
                .ordemServicoId(os.getId())
                .escalaId(cmd.escalaId())
                .categoria(cmd.tipoDespacho())
                .descricaoTarefa(cmd.descricaoTarefa())
                .status(SituacaoDespacho.EM_ANDAMENTO)
                .dataInicio(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .latitude(cmd.latitude())
                .longitude(cmd.longitude())
                .build();
        despachoPort.salvar(despacho);

        return os;
    }
}
