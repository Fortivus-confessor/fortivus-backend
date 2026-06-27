package br.arthconf.fortivus.application.port.out;

public interface IdentityManagementPort {
    void criarUsuario(String email, String senha, String nome, String role);
    void atualizarUsuario(String emailAntigo, String novoEmail, String nome, String role);
    void deletarUsuario(String email);
    void atualizarSenha(String email, String novaSenha);
}
