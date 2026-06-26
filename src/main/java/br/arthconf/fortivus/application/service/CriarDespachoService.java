package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.CriarDespachoUseCase;
import br.arthconf.fortivus.application.port.out.DespachoRepositoryPort;
import br.arthconf.fortivus.domain.model.Despacho;
import br.arthconf.fortivus.infrastructure.messaging.MobileNotificationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class CriarDespachoService implements CriarDespachoUseCase {

    private final DespachoRepositoryPort despachoPort;
    private final MobileNotificationProducer mobileProducer;

    @Override
    public Despacho executar(Despacho despacho) {
        if (despacho.getId() == null) {
            despacho.setDataInicio(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        }
        Despacho salvo = despachoPort.salvar(despacho);
        mobileProducer.publicarDespachoAtribuido(salvo);
        return salvo;
    }
}
