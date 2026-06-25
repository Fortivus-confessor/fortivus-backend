package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.GerenciarEquipeUseCase;
import br.arthconf.fortivus.application.port.in.ListarEquipesUseCase;
import br.arthconf.fortivus.application.port.in.ObterUsuarioLogadoUseCase;
import br.arthconf.fortivus.application.port.out.EquipePort;
import br.arthconf.fortivus.domain.PerfilAcesso;
import br.arthconf.fortivus.domain.model.Equipe;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EquipeApplicationService implements ListarEquipesUseCase, GerenciarEquipeUseCase {

    private final EquipePort equipePort;
    private final ObterUsuarioLogadoUseCase obterUsuarioLogadoUseCase;

    @Override
    @Transactional(readOnly = true)
    public List<Equipe> listarTodas() {
        var usuario = obterUsuarioLogadoUseCase.getUsuarioLogado();
        if (usuario != null && usuario.getPerfil() == PerfilAcesso.ROLE_CENTRO_COMANDO
                && usuario.getCentroComando() != null) {
            return buscarPorCentro(usuario.getCentroComando().getId());
        }
        List<Equipe> lista = equipePort.findAllComCentro();
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipe> buscarPorCentro(UUID centroId) {
        var lista = equipePort.findByCentroComandoId(centroId);
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Override
    @Transactional
    public Equipe salvar(Equipe equipe) {
        return equipePort.save(equipe);
    }

    @Override
    @Transactional(readOnly = true)
    public Equipe buscarPorId(UUID id) {
        return equipePort.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipe não encontrada"));
    }

    @Override
    @Transactional
    public void deletar(UUID id) {
        equipePort.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Equipe> listarPaginado(Pageable pageable) {
        var usuario = obterUsuarioLogadoUseCase.getUsuarioLogado();
        if (usuario != null && usuario.getPerfil() == PerfilAcesso.ROLE_CENTRO_COMANDO
                && usuario.getCentroComando() != null) {
            return equipePort.findByCentroComandoId(usuario.getCentroComando().getId(), pageable);
        }
        return equipePort.findAll(pageable);
    }
}
