package br.arthconf.fortivus.domain.model;

import br.arthconf.fortivus.domain.EstadoEquipamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Equipamento {
    private UUID id;
    private String nome;
    private String identificador;
    private EstadoEquipamento estado;
    private UUID equipeId;
}
