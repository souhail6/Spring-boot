package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IPaiementRepository;

@Service
@RequiredArgsConstructor
public class ContratServiceImpl implements IContratService {
    private final IContratRepository repository;
    private final IPaiementRepository paiementRepository;

    @Override
    public Contrat create(Contrat entity) {
        if (entity.getIdContrat() != null) {
            throw new IllegalArgumentException("Une nouvelle ressource ne doit pas avoir d'identifiant");
        }
        return repository.save(entity);
    }

    @Override
    public Contrat findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }

    @Override
    public List<Contrat> findAll() {
        return repository.findAll();
    }

    @Override
    public Contrat update(Long id, Contrat entity) {
        Contrat existing = findById(id);
        BeanUtils.copyProperties(entity, existing, "idContrat");
        return repository.save(existing);
    }

    @Override
    public void deleteById(Long id) {
        repository.delete(findById(id));
    }

    @Override
    public Paiement addPaiement(Long contratId, Paiement paiement) {
        if (paiement.getIdPaiement() != null) {
            throw new IllegalArgumentException("Un nouveau paiement ne doit pas avoir d'identifiant");
        }
        Contrat contrat = findById(contratId);
        paiement.setContrat(contrat);
        contrat.getPaiements().add(paiement);
        return paiementRepository.save(paiement);
    }

    @Override
    public void removePaiement(Long contratId, Long paiementId) {
        Contrat contrat = findById(contratId);
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement", paiementId));
        if (!contrat.getPaiements().remove(paiement)) {
            throw new ResourceNotFoundException("Paiement du contrat", paiementId);
        }
        paiementRepository.delete(paiement);
    }
}
