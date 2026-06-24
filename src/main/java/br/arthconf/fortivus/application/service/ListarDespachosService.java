package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.usecase.ListarDespachosUseCase;
import br.arthconf.fortivus.dto.DespachoDTO;
import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.repository.DespachoRepository;
import br.arthconf.fortivus.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarDespachosService implements ListarDespachosUseCase {

    private final DespachoRepository despachoRepository;
    private final UsuarioService usuarioService;

    @Override
    @Transactional(readOnly = true)
    public List<DespachoDTO> listarTodos() {
        br.arthconf.fortivus.domain.model.Usuario logado = usuarioService.getUsuarioLogado();
        List<DespachoEntity> lista;
        if (logado != null) {
            String role = logado.getPerfil().name();
            if ("ROLE_CENTRO_COMANDO".equals(role)) {
                lista = despachoRepository.findAllByCentroComandoIdList(logado.getCentroComando().getId());
            } else if ("ROLE_COMBATENTE".equals(role)) {
                lista = despachoRepository.findAllByCombatenteIdList(logado.getId());
            } else {
                lista = despachoRepository.findAllWithDetails();
            }
        } else {
            lista = despachoRepository.findAllWithDetails();
        }
        
        if (lista == null) {
            return new ArrayList<>();
        }
        
        return lista.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DespachoDTO> listarPaginado(Pageable pageable) {
        br.arthconf.fortivus.domain.model.Usuario logado = usuarioService.getUsuarioLogado();
        Page<DespachoEntity> page;
        if (logado != null) {
            String role = logado.getPerfil().name();
            if ("ROLE_CENTRO_COMANDO".equals(role)) {
                page = despachoRepository.findAllByCentroComandoId(logado.getCentroComando().getId(), pageable);
            } else if ("ROLE_COMBATENTE".equals(role)) {
                page = despachoRepository.findAllByCombatenteId(logado.getId(), pageable);
            } else {
                page = despachoRepository.findAll(pageable);
            }
        } else {
            page = despachoRepository.findAll(pageable);
        }
        return page.map(this::toDTO);
    }

    private DespachoDTO toDTO(DespachoEntity entity) {
        return new DespachoDTO(
                entity.getId(),
                entity.getOrdemServico() != null ? entity.getOrdemServico().getId() : null,
                entity.getEscala() != null ? entity.getEscala().getId() : null,
                entity.getResponsavel() != null ? entity.getResponsavel().getId() : null,
                entity.getCategoria(),
                entity.getDescricaoTarefa(),
                entity.getStatus(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getLatitude(),
                entity.getLongitude()
        );
    }
}
