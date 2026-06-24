package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.output.DespachoRepositoryPort;
import br.arthconf.fortivus.application.usecase.AtualizarStatusDespachoUseCase;
import br.arthconf.fortivus.domain.SituacaoDespacho;
import br.arthconf.fortivus.domain.model.Despacho;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import br.arthconf.fortivus.service.UsuarioService;
import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.repository.DespachoRepository;
@Service
@RequiredArgsConstructor
public class AtualizarStatusDespachoService implements AtualizarStatusDespachoUseCase {

    private final DespachoRepositoryPort despachoPort;
    private final DespachoRepository despachoRepository;
    private final UsuarioService usuarioService;

    @Override
    public void executar(Long id, SituacaoDespacho novoStatus) {
        Despacho despacho = despachoPort.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Despacho não encontrado"));

        br.arthconf.fortivus.domain.model.Usuario logado = usuarioService.getUsuarioLogado();
        if (logado != null && "ROLE_COMBATENTE".equals(logado.getPerfil().name())) {
            DespachoEntity entity = despachoRepository.findById(id).orElseThrow();
            boolean isIntegrante = false;
            if (entity.getEscala() != null) {
                if (entity.getEscala().getComandante().getId().equals(logado.getId())) {
                    isIntegrante = true;
                } else {
                    isIntegrante = entity.getEscala().getIntegrantes().stream().anyMatch(i -> i.getId().equals(logado.getId()));
                }
            }
            if (!isIntegrante) {
                throw new org.springframework.security.access.AccessDeniedException("Você só pode atualizar o status dos seus próprios despachos.");
            }
        }

        if (novoStatus == SituacaoDespacho.CONCLUIDO) {
            despacho.finalizar(LocalDateTime.now(java.time.ZoneId.of("America/Sao_Paulo")));
        } else {
            despacho.setStatus(novoStatus);
        }

        despachoPort.salvar(despacho);
    }
}
