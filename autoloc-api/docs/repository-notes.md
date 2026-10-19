# Atelier 3 — Couche Repository

Les repositories se trouvent dans `tn.esprit.autoloc.repository`. Chaque interface relie une entité JPA à son identifiant (`Long`) et hérite des opérations CRUD fournies par Spring Data.

## Choix de l’interface de base

| Base | Fonctionnalités | Choix pour cet atelier |
| --- | --- | --- |
| `CrudRepository<T, ID>` | CRUD de base : sauvegarder, rechercher par identifiant, supprimer, compter et parcourir les entités. | Suffit si l’application a seulement besoin des opérations CRUD. C’était le choix possible pour `IContratRepository` à la première étape. |
| `PagingAndSortingRepository<T, ID>` | CRUD et capacités de pagination/tri selon la version de Spring Data. | À choisir lorsque le code utilise explicitement la pagination ou le tri via cette abstraction. |
| `JpaRepository<T, ID>` | Opérations CRUD, pagination/tri, et opérations spécifiques à JPA telles que `flush()` et `saveAndFlush()`. | Choix retenu pour les neuf interfaces : interface complète et adaptée à la persistance JPA utilisée dans ce projet. |

`JpaRepository` étend les abstractions Spring Data de plus bas niveau. Le projet peut donc employer directement une seule base cohérente pour toutes ses entités. Une interface de repository reste déclarative : Spring Data crée son implémentation et son proxy au démarrage, sans classe d’implémentation écrite à la main.

## Les neuf repositories

Tous sont dans `src/main/java/tn/esprit/autoloc/repository/`. Le modèle est le même pour chaque entité :

```java
package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Contrat;

public interface IContratRepository extends JpaRepository<Contrat, Long> {
}
```

Pour les autres interfaces, remplace `Contrat` par l’entité correspondante :

| Interface | Entité | Type d’identifiant |
| --- | --- | --- |
| `IAgenceRepository` | `Agence` | `Long` |
| `IEmployeRepository` | `Employe` | `Long` |
| `IVehiculeRepository` | `Vehicule` | `Long` |
| `IEquipementRepository` | `Equipement` | `Long` |
| `IClientRepository` | `Client` | `Long` |
| `IReservationRepository` | `Reservation` | `Long` |
| `IContratRepository` | `Contrat` | `Long` |
| `IPaiementRepository` | `Paiement` | `Long` |
| `IMaintenanceRepository` | `Maintenance` | `Long` |

La forme générique est `JpaRepository<Entite, TypeIdentifiant>`. Par exemple, `IVehiculeRepository extends JpaRepository<Vehicule, Long>`.

## Méthodes courantes

| Méthode | Utilisation |
| --- | --- |
| `save(entite)` | Insère une nouvelle entité ou met à jour une entité existante selon son état/identifiant. Retourne l’instance persistée; récupérer la valeur de retour est une bonne habitude. La transaction est généralement gérée par le service. |
| `findById(id)` | Recherche par clé primaire et retourne `Optional<Entite>`; traiter le cas absent, par exemple avec `orElseThrow(...)`. |
| `deleteById(id)` | Supprime l’entité correspondant à l’identifiant. Si l’identifiant est inconnu, le comportement dépend de la version et du contexte; vérifier l’existence si l’API doit retourner une erreur métier précise. |

Exemple d’usage dans un service :

```java
Contrat saved = contratRepository.save(contrat);
Contrat found = contratRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Contrat introuvable"));
contratRepository.deleteById(id);
```

## Contrôle SonarQube for IDE (SonarLint)

Les constats exacts dépendent de la version du plugin, du profil/règles Sonar et de l’analyse du projet. Ce tableau décrit les points à vérifier; il ne prétend pas que chaque point est actuellement signalé.

| Point à examiner | Pourquoi ça peut poser problème | Correction recommandée |
| --- | --- | --- |
| Imports inutilisés | Bruit et code mort, souvent signalés par l’IDE/analyseur. | Supprimer l’import inutilisé avec l’action d’optimisation des imports de l’IDE. |
| `@Data` de Lombok sur une entité JPA | Génère notamment `equals`, `hashCode` et `toString` sur tous les champs; les relations peuvent provoquer récursion, chargements paresseux ou égalité instable. | Préférer des getters/setters ciblés (`@Getter`, `@Setter`) et définir prudemment l’égalité; exclure les relations de `toString` si nécessaire. |
| Noms non conformes | Dérogent aux conventions Java et nuisent à la lisibilité (par exemple interface sans préfixe `I` si la convention du cours l’exige). | Appliquer la convention du projet/cours de manière cohérente : ici `I` + nom métier + `Repository`. Note : la convention Java courante ne requiert pas le préfixe `I` pour les interfaces; ce préfixe est une exigence de cet atelier. |
| Types bruts ou identifiant générique incorrect | Perd la vérification de types (ou rend les opérations mal typées). | Déclarer explicitement `JpaRepository<Entite, Long>` et vérifier que `Long` correspond au type de la clé primaire. |
| Méthodes CRUD copiées dans l’interface | Redéclarer sans besoin les méthodes héritées augmente le code et peut créer des incohérences. | Garder les interfaces vides tant qu’aucune requête métier spécifique n’est requise. |
| Usage inadapté de `Optional` | `findById` peut ne rien trouver; ignorer ce cas mène souvent à une erreur ultérieure. | Traiter explicitement l’absence dans le service ou le contrôleur. |

