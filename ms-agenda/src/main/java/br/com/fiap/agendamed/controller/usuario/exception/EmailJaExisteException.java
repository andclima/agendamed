package br.com.fiap.agendamed.controller.usuario.exception;

public class EmailJaExisteException extends RuntimeException{
    public EmailJaExisteException(String message) {
        super(message);
    }
}
