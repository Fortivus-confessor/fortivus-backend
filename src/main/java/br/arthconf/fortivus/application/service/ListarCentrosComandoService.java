package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.ListarCentrosComandoUseCase;
import br.arthconf.fortivus.application.port.out.CentroComandoRepositoryPort;
import br.arthconf.fortivus.domain.model.CentroComando;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarCentrosComandoService implements ListarCentrosComandoUseCase {

    private final CentroComandoRepositoryPort centroPort;

    @Override
    @Transactional(readOnly = true)
    public List<CentroComando> listarTodos() {
        return centroPort.listarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CentroComando> listarPaginado(Pageable pageable) {
        return centroPort.listarPaginado(pageable);
    }
}
