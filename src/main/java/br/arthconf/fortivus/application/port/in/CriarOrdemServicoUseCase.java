package br.arthconf.fortivus.application.port.in;

import br.arthconf.fortivus.domain.model.CategoriaOperacao;
import br.arthconf.fortivus.domain.model.OrdemServico;

import java.util.UUID;

public interface CriarOrdemServicoUseCase {

    record Command(
            String descricaoTarefa,
            Long eventoFogoId,
            UUID escalaId,
            UUID responsavelId,
            CategoriaOperacao tipoDespacho,
            Double latitude,
            Double longitude
    ) {}

    OrdemServico executar(Command command);
}
