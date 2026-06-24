package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.model.Equipe;
import br.arthconf.fortivus.application.port.out.EquipePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class EquipeService {

    private final EquipePort equipePort;
    private final br.arthconf.fortivus.service.UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<Equipe> listarTodas() {
        br.arthconf.fortivus.domain.model.Usuario usuario = usuarioService.getUsuarioLogado();
        if (usuario != null && usuario.getPerfil() == br.arthconf.fortivus.domain.PerfilAcesso.ROLE_CENTRO_COMANDO && usuario.getCentroComando() != null) {
            return buscarPorCentro(usuario.getCentroComando().getId());
        }
        
        // Busca com JOIN FETCH já definido no Repository
        List<Equipe> lista = equipePort.findAllComCentro();
        // Converte para ArrayList pura para garantir que o Thymeleaf 
        // não tente disparar Lazy Loading fora da transação.
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<Equipe> buscarPorCentro(UUID centroId) {
        var lista = equipePort.findByCentroComandoId(centroId);
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Transactional
    public Equipe salvar(Equipe equipe) {
        return equipePort.save(equipe);
    }

    @Transactional(readOnly = true)
    public Equipe buscarPorId(UUID id) {
        return equipePort.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipe não encontrada"));
    }

    @Transactional
    public void deletar(UUID id) {
        equipePort.deleteById(id);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Equipe> listarPaginado(org.springframework.data.domain.Pageable pageable) {
        br.arthconf.fortivus.domain.model.Usuario usuario = usuarioService.getUsuarioLogado();
        if (usuario != null && usuario.getPerfil() == br.arthconf.fortivus.domain.PerfilAcesso.ROLE_CENTRO_COMANDO && usuario.getCentroComando() != null) {
            return equipePort.findByCentroComandoId(usuario.getCentroComando().getId(), pageable);
        }
        return equipePort.findAll(pageable);
    }
}
