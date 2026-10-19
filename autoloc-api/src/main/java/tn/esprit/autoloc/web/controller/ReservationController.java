package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.service.IReservationService;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Gestion des reservations")
public class ReservationController {
    private final IReservationService service;

    @GetMapping
@Operation(summary = "Lister les reservations")
@ApiResponse(responseCode = "200", description = "Liste retournee")
public List<Reservation> findAll() { return service.findAll(); }
    @GetMapping("/{id}")
@Operation(summary = "Rechercher par identifiant")
@ApiResponses({@ApiResponse(responseCode = "200", description = "Ressource trouvee"), @ApiResponse(responseCode = "404", description = "Ressource introuvable")})
public Reservation findById(@Parameter(description = "Identifiant", required = true) @PathVariable Long id) { return service.findById(id); }
    @PostMapping
@Operation(summary = "Creer une ressource")
@ApiResponses({@ApiResponse(responseCode = "201", description = "Ressource creee"), @ApiResponse(responseCode = "400", description = "Requete invalide")})
public ResponseEntity<Reservation> create(@RequestBody Reservation entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entity));
    }
    @PutMapping("/{id}")
@Operation(summary = "Modifier une ressource")
@ApiResponses({@ApiResponse(responseCode = "200", description = "Ressource modifiee"), @ApiResponse(responseCode = "404", description = "Ressource introuvable")})
public Reservation update(@Parameter(description = "Identifiant", required = true) @PathVariable Long id, @RequestBody Reservation entity) {
        return service.update(id, entity);
    }
    @DeleteMapping("/{id}")
@Operation(summary = "Supprimer une ressource")
@ApiResponses({@ApiResponse(responseCode = "204", description = "Ressource supprimee"), @ApiResponse(responseCode = "404", description = "Ressource introuvable")})
public ResponseEntity<Void> deleteById(@Parameter(description = "Identifiant", required = true) @PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
