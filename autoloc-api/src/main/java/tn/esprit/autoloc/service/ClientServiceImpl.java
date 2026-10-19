package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IClientRepository;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {
    private final IClientRepository repository;

    @Override
    public Client create(Client entity) {
        if (entity.getIdClient() != null) {
            throw new IllegalArgumentException("Une nouvelle ressource ne doit pas avoir d'identifiant");
        }
        return repository.save(entity);
    }

    @Override
    public Client findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    @Override
    public List<Client> findAll() {
        return repository.findAll();
    }

    @Override
    public Client update(Long id, Client entity) {
        Client existing = findById(id);
        BeanUtils.copyProperties(entity, existing, "idClient");
        return repository.save(existing);
    }

    @Override
    public void deleteById(Long id) {
        repository.delete(findById(id));
    }
}

