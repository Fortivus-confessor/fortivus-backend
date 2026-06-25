package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.domain.model.OrdemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoRepositoryPort {
    OrdemServico salvar(OrdemServico ordemServico);
    Optional<OrdemServico> buscarPorId(Long id);
    void deletar(Long id);
    boolean existe(Long id);
    boolean temDespachos(Long osId);
    Optional<Long> findMaxId(Long minId, Long maxId);
    List<OrdemServico> listarTodas();
    List<OrdemServico> listarPorCentroComando(UUID centroId);
    List<OrdemServico> listarPorCombatente(UUID usuarioId);
    Page<OrdemServico> listarPaginado(Pageable pageable);
    Page<OrdemServico> listarPorCentroComandoPaginado(UUID centroId, Pageable pageable);
    Page<OrdemServico> listarPorCombatentePaginado(UUID usuarioId, Pageable pageable);
    List<OrdemServico> listarEmExecucaoComDespachos();
    long count();
}
