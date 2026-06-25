package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.SalvarCentroComandoUseCase;
import br.arthconf.fortivus.application.port.output.CentroComandoRepositoryPort;
import br.arthconf.fortivus.domain.model.CentroComando;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalvarCentroComandoService implements SalvarCentroComandoUseCase {

    private final CentroComandoRepositoryPort centroPort;

    @Override
    @Transactional
    public CentroComando executar(CentroComando centroComando) {
        return centroPort.salvar(centroComando);
    }
}
