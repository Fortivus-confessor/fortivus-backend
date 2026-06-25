package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.out.EquipePort;
import br.arthconf.fortivus.application.port.out.OrdemServicoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EquipePort equipePort;
    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;

    public Map<String, Object> getKpis() {
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("eventosAtivos", 5); // Placeholder — real value comes from fire-event-service
        kpis.put("equipesCampo", equipePort.count());
        kpis.put("osAbertas", ordemServicoRepositoryPort.count());
        kpis.put("ativosOperantes", "100%");
        return kpis;
    }
}
