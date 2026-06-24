package br.arthconf.fortivus.infrastructure.persistence.adapter;

import br.arthconf.fortivus.application.port.output.OrdemServicoRepositoryPort;
import br.arthconf.fortivus.domain.model.OrdemServico;
import br.arthconf.fortivus.infrastructure.persistence.entity.OrdemServicoEntity;
import br.arthconf.fortivus.infrastructure.persistence.mapper.OrdemServicoMapper;
import br.arthconf.fortivus.repository.OrdemServicoRepository;
import br.arthconf.fortivus.repository.EscalaRepository;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrdemServicoPersistenceAdapter implements OrdemServicoRepositoryPort {

    private final OrdemServicoRepository repository;
    private final OrdemServicoMapper mapper;
    private final EscalaRepository escalaRepository;
    private final SpringDataUsuarioRepository usuarioRepository;

    @Override
    public OrdemServico salvar(OrdemServico domain) {
        OrdemServicoEntity entity = mapper.toEntity(domain);
        
        if (domain.getEscalaId() != null) {
            entity.setEscala(escalaRepository.findById(domain.getEscalaId()).orElse(null));
        }
        if (domain.getRelatorId() != null) {
            entity.setRelator(usuarioRepository.findById(domain.getRelatorId()).orElse(null));
        }
        
        OrdemServicoEntity salvo = repository.save(entity);
        return mapper.toDomain(salvo);
    }

    @Override
    public Optional<OrdemServico> buscarPorId(Long id) {
        return repository.findByIdFetched(id).map(mapper::toDomain);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existe(Long id) {
        return repository.existsById(id);
    }
}

