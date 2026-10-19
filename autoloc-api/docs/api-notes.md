# Atelier 5 — API Web AutoLoc

## Configuration

`application.properties` définit `server.servlet.context-path=/api`. Toutes les routes HTTP sont donc préfixées par `/api`.

Le projet utilise Spring Boot **4.1.1**. SpringDoc 3.x est nécessaire pour cette version majeure; `pom.xml` ajoute `org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.1`. La consigne « compatible Spring Boot 3.x » correspond à SpringDoc 2.x, mais remplacer le Boot 4 déjà configuré aurait imposé une migration plus large. Pour un projet Boot 3, utiliser une version SpringDoc 2.x compatible avec sa version mineure.

`OpenApiConfig` fournit les métadonnées `AutoLoc API`, version `1.0.0`. SpringDoc expose le document OpenAPI et Swagger UI automatiquement. Avec le context path, consulter `/api/v3/api-docs` et `/api/swagger-ui/index.html` (ou `/api/swagger-ui.html`, redirection selon la version).

## End-points

Toutes les routes utilisent le préfixe `/api`.

| Ressource | GET collection | GET par ID | POST | PUT par ID | DELETE par ID |
| --- | --- | --- | --- | --- | --- |
| Contrat | `/contrats` | `/contrats/{id}` | `/contrats` | `/contrats/{id}` | `/contrats/{id}` |
| Paiement | `/paiements` | `/paiements/{id}` | — | — | — |
| Agence | `/agences` | `/agences/{id}` | `/agences` | `/agences/{id}` | `/agences/{id}` |
| Employé | `/employes` | `/employes/{id}` | `/employes` | `/employes/{id}` | `/employes/{id}` |
| Véhicule | `/vehicules` | `/vehicules/{id}` | `/vehicules` | `/vehicules/{id}` | `/vehicules/{id}` |
| Équipement | `/equipements` | `/equipements/{id}` | `/equipements` | `/equipements/{id}` | `/equipements/{id}` |
| Client | `/clients` | `/clients/{id}` | `/clients` | `/clients/{id}` | `/clients/{id}` |
| Réservation | `/reservations` | `/reservations/{id}` | `/reservations` | `/reservations/{id}` | `/reservations/{id}` |
| Maintenance | `/maintenances` | `/maintenances/{id}` | `/maintenances` | `/maintenances/{id}` | `/maintenances/{id}` |

Le chemin complet de la lecture des contrats est par exemple `GET http://localhost:8080/api/contrats`.

| Code | Sens dans cette API |
| --- | --- |
| `200 OK` | Lecture ou modification réussie. |
| `201 Created` | Création réussie. |
| `204 No Content` | Suppression réussie, sans corps de réponse. |
| `400 Bad Request` | JSON invalide ou identifiant fourni lors d’une création (rejeté par le service). |
| `404 Not Found` | Identifiant demandé absent, transformé par `ApiExceptionHandler`. |
| `405 Method Not Allowed` | Méthode HTTP non exposée pour la route, par exemple `POST /paiements`. Spring MVC répond automatiquement. |

Les contrôleurs injectent les interfaces de service via `@RequiredArgsConstructor`, et délèguent la logique métier aux implémentations de l’Atelier 4. `PaiementController` est en lecture seule. L’ajout/retrait des paiements est porté par les routes de gestion du contrat déjà disponibles et les opérations service correspondantes; la relation est détenue par `Contrat.paiements`.

## Annotations Jackson et cascade JPA

`Contrat.paiements` est une collection `@OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL, orphanRemoval = true)`. `ALL` comprend `PERSIST` et `REMOVE`: la persistance du contrat peut propager la persistance des paiements, et la suppression du contrat supprime ses paiements. `Paiement.contrat` est le côté propriétaire (`@ManyToOne`).

Les relations des entités utilisent actuellement `@JsonProperty(access = WRITE_ONLY)` pour empêcher la sérialisation de plusieurs références inverses/lazy. `Paiement.contrat` est également write-only : la réponse paiement ne ré-embarque donc pas le contrat et évite une boucle Contrat ↔ Paiement. Les champs relationnels write-only peuvent être fournis en entrée JSON, mais l’API actuelle ne garantit pas que les IDs relationnels arbitraires soient chargés/validés comme références métier; ce contrôle appartient au service.

`@JsonManagedReference`/`@JsonBackReference` est une alternative pour les relations parent/enfant, à appliquer en paire. `@JsonIgnore` convient si une relation doit être ignorée dans les deux sens. `WRITE_ONLY` est le choix actuel pour limiter les payloads et éviter l’exposition des proxies lazy. Pour une API durable, des DTOs explicites sont préférables à exposer directement des entités JPA.

## Annotations OpenAPI

Les contrôleurs utilisent `@Tag` pour regrouper une ressource dans Swagger UI, `@Operation` pour résumer l’action, `@ApiResponse(s)` pour documenter les codes de retour et `@Parameter` pour décrire les identifiants de chemin. `OpenApiConfig` déclare le titre et la version de l’API.

Exemple de lecture par ID :

```java
@GetMapping("/{id}")
@Operation(summary = "Rechercher un contrat par identifiant")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Contrat trouve"),
    @ApiResponse(responseCode = "404", description = "Contrat introuvable")
})
public Contrat findById(
        @Parameter(description = "Identifiant du contrat", required = true)
        @PathVariable Long id) {
    return contratService.findById(id);
}
```

## Postman et Swagger UI

Dans Postman, définir `baseUrl = http://localhost:8080/api`. Pour les opérations JSON, envoyer `Content-Type: application/json`.

| Scénario | Requête | Résultat attendu |
| --- | --- | --- |
| Liste nominale | `GET {{baseUrl}}/contrats` | `200`, tableau JSON. |
| Lecture nominale | `GET {{baseUrl}}/contrats/1` | `200` avec un contrat existant. |
| Création nominale | `POST {{baseUrl}}/contrats`, body `{"dateSignature":"2026-10-19","montantTotal":120.00,"valide":true}` | `201` et entité créée (les champs métier obligatoires dépendent du schéma). |
| Modification nominale | `PUT {{baseUrl}}/contrats/1`, JSON avec les champs à modifier | `200` et entité mise à jour. |
| Suppression nominale | `DELETE {{baseUrl}}/contrats/1` | `204`, corps vide. |
| 404 | `GET {{baseUrl}}/contrats/999999` | `404`, objet JSON `timestamp/status/message`. |
| 400 JSON | `POST {{baseUrl}}/contrats` avec un JSON mal formé, par exemple `{` | `400 Bad Request` de Spring MVC. |
| 400 création | `POST {{baseUrl}}/contrats` avec un `idContrat` non nul | `400`, rejet explicite du service. |
| 405 lecture seule | `POST {{baseUrl}}/paiements` | `405 Method Not Allowed`. |

Dans Swagger UI, ouvrir `/api/swagger-ui/index.html`, développer un tag et sélectionner **Try it out**. Vérifier que la requête cible le préfixe `/api` et que la base MySQL est disponible avant les essais qui lisent ou écrivent des données.

## Anomalies SonarQube for IDE et réponses

| Anomalie à vérifier | Réponse / correction |
| --- | --- |
| Imports inutilisés | Laisser l’IDE optimiser les imports; vérifier après ajout/retrait d’annotations. |
| Contrôleurs trop similaires ou méthodes sur une ligne | Extraire une base/générique seulement si cela améliore réellement la lisibilité; conserver des routes explicites pour OpenAPI. Formater méthodes sur plusieurs lignes. |
| Entités JPA exposées comme payload | Pour l’atelier, les contrôleurs reprennent les types des services. En production, ajouter des DTOs et mapper les champs pour isoler le modèle de persistance. |
| `@Data` Lombok sur une entité | À éviter sur les entités JPA : `equals/hashCode/toString` peut parcourir des relations lazy et créer des cycles. Les entités actuelles utilisent getters/setters ciblés. |
| Accès aux propriétés lazy après fermeture de session | Préférer DTO/projections ou chargement explicite dans le service; ne pas forcer toutes les relations en eager. Les annotations Jackson seules ne règlent pas chaque cas proxy. |
| Codes de retour non documentés ou non cohérents | Documenter les cas courants avec `@ApiResponse`; `ApiExceptionHandler` transforme les erreurs métier en 400/404. |
| Nom interface sans préfixe `I` | Convention imposée par le projet/cours pour les services; Java n’exige pas ce préfixe. |

