package br.arthconf.fortivus.domain;

import java.text.Normalizer;

public enum CategoriaOperacao {
    TERRESTRE,
    AEREO,
    MAQUINARIO,
    AQUATICO;

    public static CategoriaOperacao fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toUpperCase();
        return CategoriaOperacao.valueOf(normalized);
    }
}
