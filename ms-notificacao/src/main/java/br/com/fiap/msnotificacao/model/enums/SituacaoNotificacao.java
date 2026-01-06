package br.com.fiap.msnotificacao.model.enums;

public enum SituacaoNotificacao {
    PENDENTE(1), ENVIADA(2), ERRO(3);

    private final int valor;

    SituacaoNotificacao(int valor) {
        this.valor = valor;
    }

    public String toString() {
        String descricao = "";
        switch (valor) {
            case 1:
                descricao = "Pendente"; break;
            case 2:
                descricao = "Enviada"; break;
            case 3:
                descricao = "Erro"; break;
        }
        return descricao;
    }

    public static SituacaoNotificacao from(String perfil) {
        try {
            return SituacaoNotificacao.valueOf(perfil.trim().toUpperCase());
        }
        catch (Exception ex) {
            return null;
        }
    }

    public int getValor() {
        return this.valor;
    }

    public SituacaoNotificacao[] getLista() {
        return SituacaoNotificacao.values();
    }

}
