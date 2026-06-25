package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.AtualizarStatusDespachoUseCase;
import br.arthconf.fortivus.application.port.out.DespachoRepositoryPort;
import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.domain.model.Despacho;
import br.arthconf.fortivus.application.port.in.ObterUsuarioLogadoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AtualizarStatusDespachoService implements AtualizarStatusDespachoUseCase {

    private final DespachoRepositoryPort despachoPort;
    private final ObterUsuarioLogadoUseCase obterUsuarioLogadoUseCase;

    @Override
    public void executar(Long id, SituacaoDespacho novoStatus) {
        Despacho despacho = despachoPort.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Despacho não encontrado"));

        br.arthconf.fortivus.domain.model.Usuario logado = obterUsuarioLogadoUseCase.getUsuarioLogado();
        if (logado != null && "ROLE_COMBATENTE".equals(logado.getPerfil().name())) {
            if (!despachoPort.pertenceAoDespacho(id, logado.getId())) {
                throw new AccessDeniedException("Você só pode atualizar o status dos seus próprios despachos.");
            }
        }

        if (novoStatus == SituacaoDespacho.CONCLUIDO) {
            if (despacho.getStatus() != SituacaoDespacho.CONCLUIDO) {
                despacho.finalizar(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
            }
        } else {
            despacho.setStatus(novoStatus);
        }

        despachoPort.salvar(despacho);
    }
}
