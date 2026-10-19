package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "contrat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContrat;

    private LocalDate dateSignature;
    private BigDecimal montantTotal;
    private boolean valide;

    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Paiement> paiements = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reservation", unique = true)
    private Reservation reservation;

    public void addPaiement(Paiement paiement) {
        Objects.requireNonNull(paiement, "Le paiement ne doit pas etre null");
        paiements.add(paiement);
        paiement.setContrat(this);
    }

    public boolean removePaiement(Paiement paiement) {
        if (paiements.remove(paiement)) {
            paiement.setContrat(null);
            return true;
        }
        return false;
    }
}
