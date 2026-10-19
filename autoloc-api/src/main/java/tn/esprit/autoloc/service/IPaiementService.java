package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.domain.Paiement;

public interface IPaiementService {
    Paiement create(Paiement entity);
    Paiement findById(Long id);
    List<Paiement> findAll();
    Paiement update(Long id, Paiement entity);
    void deleteById(Long id);
}
