package br.arthconf.fortivus.config;

import br.arthconf.fortivus.domain.PerfilAcesso;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToPerfilAcessoConverter implements Converter<String, PerfilAcesso> {

    @Override
    public PerfilAcesso convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        String formatted = source.toUpperCase().trim();
        try {
            // Primeiro tenta a conversão direta (caso o frontend já envie "ROLE_COMBATENTE")
            return PerfilAcesso.valueOf(formatted);
        } catch (IllegalArgumentException e) {
            // Se falhar, tenta colocar o prefixo ROLE_ (ex: frontend mandou apenas "COMBATENTE")
            try {
                return PerfilAcesso.valueOf("ROLE_" + formatted);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }
}
