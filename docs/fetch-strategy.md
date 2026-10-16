# Stratégies de fetch et de cascade

Les associations sont chargées en `LAZY` pour ne lire les entités liées que lorsqu'elles sont utiles. Les collections sont initialisées et restent en `LAZY`; les cas où les données associées sont nécessaires devront les charger explicitement dans une requête adaptée.

| Association | Fetch | Cascade | Justification |
|---|---|---|---|
| Contrat → Paiements | `LAZY` | `ALL` et `orphanRemoval = true` | Un paiement dépend du contrat. Enregistrer, modifier ou supprimer le contrat propage l'opération ; retirer un paiement de la collection le supprime aussi. |
| Agence → Véhicules | `LAZY` | Aucune | Les véhicules ont un cycle de vie indépendant et peuvent survivre à leur agence. |
| Agence → Employés | `LAZY` | Aucune | Les employés ont un cycle de vie indépendant de l'agence. |
| Véhicule ↔ Équipements | `LAZY` | Aucune | Un équipement peut être partagé entre plusieurs véhicules ; aucune suppression en cascade ne doit effacer un équipement partagé. Un `Set` représente cette association sans doublons. |
| Client → Réservations | `LAZY` | `PERSIST` | Enregistrer un client peut enregistrer ses nouvelles réservations, sans propager les mises à jour ou suppressions. |
| Réservation → Véhicule | `LAZY` | Aucune | Le véhicule existe indépendamment de chaque réservation. |
| Réservation → Contrat | `LAZY` | `ALL` côté Réservation | Le contrat suit le cycle de vie de sa réservation. La clé étrangère unique est portée par Contrat (`id_reservation`). |
| Véhicule → Maintenances | `LAZY` | `PERSIST` | Enregistrer un véhicule peut créer ses maintenances, sans supprimer les historiques de maintenance lorsque le véhicule est supprimé. |
| Véhicule → Réservations | `LAZY` | Aucune | Côté inverse de Réservation → Véhicule ; les réservations sont gérées par leur propre cycle de vie. |
