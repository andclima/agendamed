package br.com.fiap.agendamed.model.enums;

public enum SituacaoUsuario {
    ATIVO(1), INATIVO(2), CANCELADO(3);

    private final int valor;

    SituacaoUsuario(int valor) {
        this.valor = valor;
    }

    public String toString() {
        String descricao = "";
        switch (valor) {
            case 1:
                descricao = "Ativo"; break;
            case 2:
                descricao = "Inativo"; break;
            case 3:
                descricao = "Cancelado"; break;
        }
        return descricao;
    }

    public static SituacaoUsuario from(String perfil) {
        try {
            return SituacaoUsuario.valueOf(perfil.trim().toUpperCase());
        }
        catch (Exception ex) {
            return null;
        }
    }

    public int getValor() {
        return this.valor;
    }

    public SituacaoUsuario[] getLista() {
        return SituacaoUsuario.values();
    }
}
