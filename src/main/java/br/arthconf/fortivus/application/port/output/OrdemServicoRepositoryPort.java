package br.arthconf.fortivus.application.port.output;

import br.arthconf.fortivus.domain.model.OrdemServico;

import java.util.Optional;

public interface OrdemServicoRepositoryPort {
    OrdemServico salvar(OrdemServico ordemServico);
    Optional<OrdemServico> buscarPorId(Long id);
    void deletar(Long id);
    boolean existe(Long id);
}
