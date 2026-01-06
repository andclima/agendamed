package br.com.fiap.agendamed.model.enums;

public enum PerfilUsuario {
    ADMIN(1), PACIENTE(2), MEDICO(3), ENFERMEIRO(4);

    private final int valor;

    PerfilUsuario(int valor) {
        this.valor = valor;
    }

    public String toString() {
        String descricao = "";
        switch (valor) {
            case 1:
                descricao = "Administrador"; break;
            case 2:
                descricao = "Paciente"; break;
            case 3:
                descricao = "Médico"; break;
            case 4:
                descricao = "Enfermeiro"; break;
        }
        return descricao;
    }

    public static PerfilUsuario from(String perfil) {
        try {
            return PerfilUsuario.valueOf(perfil.trim().toUpperCase());
        }
        catch (Exception ex) {
            return null;
        }
    }

    public int getValor() {
        return this.valor;
    }

    public PerfilUsuario[] getLista() {
        return PerfilUsuario.values();
    }

}
