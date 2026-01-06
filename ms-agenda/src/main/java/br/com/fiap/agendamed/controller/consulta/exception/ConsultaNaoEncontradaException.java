package br.com.fiap.agendamed.controller.consulta.exception;

public class ConsultaNaoEncontradaException extends RuntimeException{
    public ConsultaNaoEncontradaException(String message) {
        super(message);
    }
}
