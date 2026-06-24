package br.arthconf.fortivus.service;

import br.arthconf.fortivus.application.port.out.EquipePort;
import br.arthconf.fortivus.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EquipePort equipePort;
    private final OrdemServicoRepository ordemServicoRepository;

    public Map<String, Object> getKpis() {
        Map<String, Object> kpis = new HashMap<>();
        // In a real scenario, "eventosAtivos" could come from a HTTP call to fire-event-service
        kpis.put("eventosAtivos", 5); // Placeholder
        long equipesDisponiveis = equipePort.count();
        kpis.put("equipesCampo", equipesDisponiveis);
        kpis.put("osAbertas", ordemServicoRepository.count());
        kpis.put("ativosOperantes", "100%");
        return kpis;
    }
}
