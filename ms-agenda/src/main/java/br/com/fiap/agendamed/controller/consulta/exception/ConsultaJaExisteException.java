package br.com.fiap.agendamed.controller.consulta.exception;

public class ConsultaJaExisteException extends RuntimeException{
    public ConsultaJaExisteException(String message) {
        super(message);
    }
}
