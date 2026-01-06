package br.com.fiap.agendamed.model.enums;

public enum SituacaoConsulta {
    AGENDADA(1), REALIZADA(2), CANCELADA(3);

    private final int valor;

    SituacaoConsulta(int valor) {
        this.valor = valor;
    }

    public String toString() {
        String descricao = "";
        switch (valor) {
            case 1:
                descricao = "Agendada"; break;
            case 2:
                descricao = "Realizada"; break;
            case 3:
                descricao = "Cancelada"; break;
        }
        return descricao;
    }

    public static SituacaoConsulta from(String perfil) {
        try {
            return SituacaoConsulta.valueOf(perfil.trim().toUpperCase());
        }
        catch (Exception ex) {
            return null;
        }
    }

    public int getValor() {
        return this.valor;
    }

    public SituacaoConsulta[] getLista() {
        return SituacaoConsulta.values();
    }

}
