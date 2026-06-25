package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.ListarDespachosUseCase;
import br.arthconf.fortivus.application.port.out.DespachoRepositoryPort;
import br.arthconf.fortivus.domain.model.Despacho;
import br.arthconf.fortivus.application.port.in.ObterUsuarioLogadoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarDespachosService implements ListarDespachosUseCase {

    private final DespachoRepositoryPort despachoPort;
    private final ObterUsuarioLogadoUseCase obterUsuarioLogadoUseCase;

    @Override
    @Transactional(readOnly = true)
    public List<Despacho> listarTodos() {
        br.arthconf.fortivus.domain.model.Usuario logado = obterUsuarioLogadoUseCase.getUsuarioLogado();
        if (logado != null) {
            String role = logado.getPerfil().name();
            if ("ROLE_CENTRO_COMANDO".equals(role)) {
                return despachoPort.listarPorCentroComando(logado.getCentroComando().getId());
            } else if ("ROLE_COMBATENTE".equals(role)) {
                return despachoPort.listarPorCombatente(logado.getId());
            }
        }
        return despachoPort.listarTodas();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Despacho> listarPaginado(Pageable pageable) {
        br.arthconf.fortivus.domain.model.Usuario logado = obterUsuarioLogadoUseCase.getUsuarioLogado();
        if (logado != null) {
            String role = logado.getPerfil().name();
            if ("ROLE_CENTRO_COMANDO".equals(role)) {
                return despachoPort.listarPorCentroComandoPaginado(logado.getCentroComando().getId(), pageable);
            } else if ("ROLE_COMBATENTE".equals(role)) {
                return despachoPort.listarPorCombatentePaginado(logado.getId(), pageable);
            }
        }
        return despachoPort.listarPaginado(pageable);
    }
}
