package br.com.fiap.agendamed.controller.consulta.exception;

public class ConsultaMedicoIndisponivelException extends RuntimeException{
    public ConsultaMedicoIndisponivelException(String message) {
        super(message);
    }
}
