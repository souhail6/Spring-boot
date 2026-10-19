package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.service.IContratService;

import java.util.List;

@RestController
@RequestMapping("/contrats")
@RequiredArgsConstructor
@Tag(name = "Contrats", description = "Gestion des contrats et de leurs paiements")
public class ContratController {
    private final IContratService contratService;

    @GetMapping
    @Operation(summary = "Lister les contrats")
    @ApiResponse(responseCode = "200", description = "Liste des contrats")
    public List<Contrat> findAll() {
        return contratService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Rechercher un contrat par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat trouvé"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    public Contrat findById(@Parameter(description = "Identifiant du contrat", required = true)
                            @PathVariable Long id) {
        return contratService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Créer un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrat créé"),
            @ApiResponse(responseCode = "400", description = "Identifiant fourni pour une création")
    })
    public ResponseEntity<Contrat> create(@RequestBody Contrat contrat) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratService.create(contrat));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat modifié"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    public Contrat update(@Parameter(description = "Identifiant du contrat", required = true)
                          @PathVariable Long id, @RequestBody Contrat contrat) {
        return contratService.update(id, contrat);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contrat supprimé avec ses paiements"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identifiant du contrat", required = true) @PathVariable Long id) {
        contratService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{contratId}/paiements")
    @Operation(summary = "Ajouter un paiement au contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paiement ajouté au contrat"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    public ResponseEntity<Paiement> addPaiement(
            @Parameter(description = "Identifiant du contrat", required = true) @PathVariable Long contratId,
            @RequestBody Paiement paiement) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contratService.addPaiement(contratId, paiement));
    }

    @DeleteMapping("/{contratId}/paiements/{paiementId}")
    @Operation(summary = "Retirer un paiement du contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Paiement retiré"),
            @ApiResponse(responseCode = "404", description = "Contrat ou paiement introuvable")
    })
    public ResponseEntity<Void> removePaiement(
            @Parameter(description = "Identifiant du contrat", required = true) @PathVariable Long contratId,
            @Parameter(description = "Identifiant du paiement", required = true) @PathVariable Long paiementId) {
        contratService.removePaiement(contratId, paiementId);
        return ResponseEntity.noContent().build();
    }
}
