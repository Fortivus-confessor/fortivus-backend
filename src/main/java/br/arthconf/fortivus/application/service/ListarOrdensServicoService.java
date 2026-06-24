package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.ListarOrdensServicoUseCase;
import br.arthconf.fortivus.application.port.output.OrdemServicoRepositoryPort;
import br.arthconf.fortivus.domain.model.OrdemServico;
import br.arthconf.fortivus.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarOrdensServicoService implements ListarOrdensServicoUseCase {

    private final OrdemServicoRepositoryPort osPort;
    private final UsuarioService usuarioService;

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> listarTodas() {
        var logado = usuarioService.getUsuarioLogado();
        if (logado != null) {
            String role = logado.getPerfil().name();
            if ("ROLE_CENTRO_COMANDO".equals(role)) {
                return osPort.listarPorCentroComando(logado.getCentroComando().getId());
            } else if ("ROLE_COMBATENTE".equals(role)) {
                return osPort.listarPorCombatente(logado.getId());
            }
        }
        return osPort.listarTodas();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdemServico> listarPaginado(Pageable pageable) {
        var logado = usuarioService.getUsuarioLogado();
        if (logado != null) {
            String role = logado.getPerfil().name();
            if ("ROLE_CENTRO_COMANDO".equals(role)) {
                return osPort.listarPorCentroComandoPaginado(logado.getCentroComando().getId(), pageable);
            } else if ("ROLE_COMBATENTE".equals(role)) {
                return osPort.listarPorCombatentePaginado(logado.getId(), pageable);
            }
        }
        return osPort.listarPaginado(pageable);
    }
}
