package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.output.DespachoRepositoryPort;
import br.arthconf.fortivus.application.usecase.CriarDespachoUseCase;
import br.arthconf.fortivus.domain.model.Despacho;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CriarDespachoService implements CriarDespachoUseCase {

    private final DespachoRepositoryPort despachoPort;

    @Override
    public Despacho executar(Despacho despacho) {
        if (despacho.getId() == null) {
            despacho.setDataInicio(LocalDateTime.now(java.time.ZoneId.of("America/Sao_Paulo")));
        }

        // TODO: Mover regra de reativar OS para outro UseCase via evento de domínio no futuro.
        // O DespachoPersistenceAdapter hoje atualiza a OS no método salvar de DespachoEntity se necessário.
        
        return despachoPort.salvar(despacho);
    }
}
