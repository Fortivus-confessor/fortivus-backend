package br.arthconf.fortivus.service;

import br.arthconf.fortivus.repository.EquipeRepository;
import br.arthconf.fortivus.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EquipeRepository equipeRepository;
    private final OrdemServicoRepository ordemServicoRepository;

    public Map<String, Object> getKpis() {
        Map<String, Object> kpis = new HashMap<>();
        // In a real scenario, "eventosAtivos" could come from a HTTP call to fire-event-service
        kpis.put("eventosAtivos", 5); // Placeholder
        kpis.put("equipesCampo", equipeRepository.count());
        kpis.put("osAbertas", ordemServicoRepository.count());
        kpis.put("ativosOperantes", "100%");
        return kpis;
    }
}
