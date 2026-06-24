package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.usecase.BuscarDespachoPorIdUseCase;
import br.arthconf.fortivus.dto.DespachoDTO;
import br.arthconf.fortivus.infrastructure.persistence.entity.DespachoEntity;
import br.arthconf.fortivus.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuscarDespachoPorIdService implements BuscarDespachoPorIdUseCase {

    private final DespachoRepository despachoRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<DespachoDTO> executar(Long id) {
        return despachoRepository.findByIdFetched(id).map(this::toDTO);
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
