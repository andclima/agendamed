package br.com.fiap.agendamed.controller.usuario.exception;

public class UsuarioNaoEncontradoException extends RuntimeException{
    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }
}
