package br.com.fiap.agendamed.controller.auth.exception;

public class CredenciaisInvalidasException extends RuntimeException{
    public CredenciaisInvalidasException(String message) {
        super(message);
    }
}
