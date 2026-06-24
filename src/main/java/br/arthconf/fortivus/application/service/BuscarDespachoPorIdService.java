package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.BuscarDespachoPorIdUseCase;
import br.arthconf.fortivus.application.port.output.DespachoRepositoryPort;
import br.arthconf.fortivus.domain.model.Despacho;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuscarDespachoPorIdService implements BuscarDespachoPorIdUseCase {

    private final DespachoRepositoryPort despachoPort;

    @Override
    @Transactional(readOnly = true)
    public Optional<Despacho> executar(Long id) {
        return despachoPort.buscarPorId(id);
    }
}
