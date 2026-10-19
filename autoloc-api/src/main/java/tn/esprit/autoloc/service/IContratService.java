package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;

public interface IContratService {
    Contrat create(Contrat entity);
    Contrat findById(Long id);
    List<Contrat> findAll();
    Contrat update(Long id, Contrat entity);
    void deleteById(Long id);
    Paiement addPaiement(Long contratId, Paiement paiement);
    void removePaiement(Long contratId, Long paiementId);
}
