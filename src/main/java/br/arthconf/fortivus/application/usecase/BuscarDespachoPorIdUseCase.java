package br.arthconf.fortivus.application.usecase;

import br.arthconf.fortivus.dto.DespachoDTO;
import java.util.Optional;

public interface BuscarDespachoPorIdUseCase {
    Optional<DespachoDTO> executar(Long id);
}
