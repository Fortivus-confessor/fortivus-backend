package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.CheckoutEquipamento;
import br.arthconf.fortivus.domain.Equipamento;
import br.arthconf.fortivus.domain.Escala;
import br.arthconf.fortivus.domain.Usuario;
import br.arthconf.fortivus.repository.CheckoutEquipamentoRepository;
import br.arthconf.fortivus.repository.EquipamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CheckoutEquipamentoRepository checkoutRepository;
    private final EquipamentoRepository equipamentoRepository;

    public List<CheckoutEquipamento> listarPorEscala(UUID escalaId) {
        // Para simplificar no bootstrap, retornamos todos e filtramos na memória ou via query se necessário
        return checkoutRepository.findAll().stream()
                .filter(c -> c.getEscala().getId().equals(escalaId))
                .toList();
    }

    @Transactional
    public void registrarEmprestimo(Escala escala, UUID equipamentoId, Usuario responsavelEntrega) {
        Equipamento eq = equipamentoRepository.findById(equipamentoId)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));

        CheckoutEquipamento checkout = new CheckoutEquipamento();
        checkout.setEscala(escala);
        checkout.setEquipamento(eq);
        checkout.setResponsavelEntrega(responsavelEntrega);
        checkout.setDataEmprestimo(LocalDateTime.now());

        checkoutRepository.save(checkout);
    }

    @Transactional
    public void registrarDevolucao(UUID checkoutId, Usuario responsavelRecebimento) {
        CheckoutEquipamento checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new RuntimeException("Registro de checkout não encontrado"));

        checkout.setDataDevolucao(LocalDateTime.now());
        checkout.setResponsavelRecebimento(responsavelRecebimento);

        checkoutRepository.save(checkout);
    }
}
