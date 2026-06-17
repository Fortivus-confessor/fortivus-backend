package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.FocoIncendio;
import br.arthconf.fortivus.dto.FocoIncendioDTO;
import br.arthconf.fortivus.repository.FocoIncendioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FocoIncendioService {

    private final FocoIncendioRepository repository;

    @Transactional
    public FocoIncendioDTO registrarFocoManual(FocoIncendioDTO dto) {
        FocoIncendio foco = new FocoIncendio();
        BeanUtils.copyProperties(dto, foco, "id");
        foco.setOrigemRegistro("MANUAL");
        foco.setStatus("ATIVO");
        foco.setDataHoraDeteccao(LocalDateTime.now());
        
        FocoIncendio saved = repository.save(foco);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<FocoIncendioDTO> listarFocosAtivos() {
        return repository.findByStatusAndDataHoraDeteccaoAfter(
                "ATIVO", 
                LocalDateTime.now().minusDays(7)
        ).stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<FocoIncendioDTO> listarTodos() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    private FocoIncendioDTO toDTO(FocoIncendio foco) {
        FocoIncendioDTO dto = new FocoIncendioDTO();
        BeanUtils.copyProperties(foco, dto);
        return dto;
    }
}
