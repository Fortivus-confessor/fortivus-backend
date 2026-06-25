package br.arthconf.fortivus.infrastructure.persistence.adapter;

import br.arthconf.fortivus.application.port.output.CheckoutEquipamentoRepositoryPort;
import br.arthconf.fortivus.domain.model.CheckoutEquipamento;
import br.arthconf.fortivus.infrastructure.persistence.entity.CheckoutEquipamentoEntity;
import br.arthconf.fortivus.infrastructure.persistence.mapper.CheckoutEquipamentoMapper;
import br.arthconf.fortivus.repository.CheckoutEquipamentoRepository;
import br.arthconf.fortivus.repository.EscalaRepository;
import br.arthconf.fortivus.repository.EquipamentoRepository;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CheckoutPersistenceAdapter implements CheckoutEquipamentoRepositoryPort {

    private final CheckoutEquipamentoRepository checkoutRepository;
    private final EscalaRepository escalaRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final SpringDataUsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public CheckoutEquipamento salvar(CheckoutEquipamento checkout) {
        CheckoutEquipamentoEntity entity;
        if (checkout.getId() != null) {
            entity = checkoutRepository.findById(checkout.getId()).orElse(new CheckoutEquipamentoEntity());
        } else {
            entity = new CheckoutEquipamentoEntity();
        }

        if (checkout.getEscalaId() != null) {
            entity.setEscala(escalaRepository.findById(checkout.getEscalaId()).orElse(null));
        }
        if (checkout.getEquipamentoId() != null) {
            entity.setEquipamento(equipamentoRepository.findById(checkout.getEquipamentoId()).orElse(null));
        }
        if (checkout.getResponsavelEntregaId() != null) {
            entity.setResponsavelEntrega(usuarioRepository.findById(checkout.getResponsavelEntregaId()).orElse(null));
        }
        if (checkout.getResponsavelRecebimentoId() != null) {
            entity.setResponsavelRecebimento(usuarioRepository.findById(checkout.getResponsavelRecebimentoId()).orElse(null));
        }
        entity.setDataEmprestimo(checkout.getDataEmprestimo());
        entity.setDataDevolucao(checkout.getDataDevolucao());

        return CheckoutEquipamentoMapper.toDomain(checkoutRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CheckoutEquipamento> buscarPorId(UUID id) {
        return checkoutRepository.findById(id).map(CheckoutEquipamentoMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckoutEquipamento> listarPorEscala(UUID escalaId) {
        return CheckoutEquipamentoMapper.toDomainList(
                checkoutRepository.findAll().stream()
                        .filter(c -> c.getEscala() != null && c.getEscala().getId().equals(escalaId))
                        .toList()
        );
    }
}
