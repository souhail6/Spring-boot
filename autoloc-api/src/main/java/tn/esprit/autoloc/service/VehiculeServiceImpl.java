package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IVehiculeRepository;

@Service
@RequiredArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {
    private final IVehiculeRepository repository;

    @Override
    public Vehicule create(Vehicule entity) {
        if (entity.getIdVehicule() != null) {
            throw new IllegalArgumentException("Une nouvelle ressource ne doit pas avoir d'identifiant");
        }
        return repository.save(entity);
    }

    @Override
    public Vehicule findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    @Override
    public List<Vehicule> findAll() {
        return repository.findAll();
    }

    @Override
    public Vehicule update(Long id, Vehicule entity) {
        Vehicule existing = findById(id);
        BeanUtils.copyProperties(entity, existing, "idVehicule");
        return repository.save(existing);
    }

    @Override
    public void deleteById(Long id) {
        repository.delete(findById(id));
    }
}

