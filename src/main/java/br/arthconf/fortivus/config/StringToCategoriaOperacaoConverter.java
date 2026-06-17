package br.arthconf.fortivus.config;

import br.arthconf.fortivus.domain.CategoriaOperacao;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToCategoriaOperacaoConverter implements Converter<String, CategoriaOperacao> {

    @Override
    public CategoriaOperacao convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            return CategoriaOperacao.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
