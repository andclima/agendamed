package br.com.fiap.msnotificacao.service;

import br.com.fiap.msnotificacao.model.Notificacao;
import br.com.fiap.msnotificacao.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository repository;

    public Notificacao create(Notificacao notificacao) {
        return repository.save(notificacao);
    }

    public Notificacao update(Notificacao notificacao) {
        return repository.save(notificacao);
    }

    public void delete(Notificacao notificacao) {
        repository.deleteById(notificacao.getId());
    }

    public Optional<Notificacao> findById(UUID id) {
        return repository.findById(id);
    }

    public Iterable<Notificacao> findAll() {
        return repository.findAll();
    }
}
