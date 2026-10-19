package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IMaintenanceRepository;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements IMaintenanceService {
    private final IMaintenanceRepository repository;

    @Override
    public Maintenance create(Maintenance entity) {
        if (entity.getIdMaintenance() != null) {
            throw new IllegalArgumentException("Une nouvelle ressource ne doit pas avoir d'identifiant");
        }
        return repository.save(entity);
    }

    @Override
    public Maintenance findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
    }

    @Override
    public List<Maintenance> findAll() {
        return repository.findAll();
    }

    @Override
    public Maintenance update(Long id, Maintenance entity) {
        Maintenance existing = findById(id);
        BeanUtils.copyProperties(entity, existing, "idMaintenance");
        return repository.save(existing);
    }

    @Override
    public void deleteById(Long id) {
        repository.delete(findById(id));
    }
}

