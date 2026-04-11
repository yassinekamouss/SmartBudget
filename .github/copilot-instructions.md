# Instructions pour SmartBudget (Android Project)

Tu es un expert Android spécialisé dans la Clean Architecture et le développement Offline-first. Tu dois guider le développement de l'application **SmartBudget**.

## 1. Principes d'Architecture & Qualité
- **Architecture :** MVVM obligatoire. [cite_start]Séparation stricte entre `Data` (Room), `Domain` (UseCases/Entities), et `UI` (Compose ViewModels/Screens)[cite: 41].
- **Injection de Dépendances :** Utilise systématiquement Hilt.
- **Gestion d'État :** Utilise `StateFlow` dans les ViewModels pour exposer l'état à l'UI Compose.
- **Clean Code :** Pas de logique métier dans les Composables. [cite_start]Utilise des UseCases pour les calculs de statistiques et les validations[cite: 9, 66].

## 2. Règles Métier Critiques (Source: PDF)
- [cite_start]**Validation :** Les montants doivent être strictement positifs[cite: 67].
- [cite_start]**Dates :** La date et la catégorie sont obligatoires pour chaque dépense[cite: 68, 69].
- [cite_start]**Offline-first :** L'application doit fonctionner sans internet, la base Room est la source de vérité unique[cite: 33, 34].
- [cite_start]**Suppression :** Si une catégorie est supprimée, propose soit de l'interdire, soit de basculer les dépenses vers la catégorie "Autre"[cite: 71, 72, 73].

## 3. Spécifications du Modèle de Données
### [cite_start]Expense (Dépense) [cite: 42]
- [cite_start]`id`, `amount` (Double/BigDecimal), `currency` (par défaut MAD)[cite: 43, 44, 45].
- [cite_start]`date` (LocalDate), `categoryId`, `note`, `paymentMethod`[cite: 46, 51, 52, 53].
- [cite_start]`createdAt`, `updatedAt` (timestamps auto-gérés)[cite: 54, 55].

### [cite_start]Category (Catégorie) [cite: 56]
- [cite_start]`id`, `name` (unique), `icon` (emoji/icon name), `color`, `isActive`[cite: 57, 58, 59, 60, 61].
- [cite_start]Catégories par défaut : Alimentation, Transport, Logement, Santé, Loisirs, Études, Autre[cite: 25].

## 4. UI & UX (Material 3)
- [cite_start]Navigation via Bottom Bar : Dépenses, Stats, Paramètres[cite: 76].
- [cite_start]Utilise des formulaires avec erreurs inline pour le montant et la date[cite: 90, 93].
- Supporte nativement le Dark Mode et les polices lisibles.

## 5. Livrables & Démo
- [cite_start]Assure-toi que le code permet de générer des jeux de tests (30 dépenses sur 2 mois) pour la démo[cite: 104, 105, 106].
- [cite_start]Prépare le code pour l'exportation des données en format CSV[cite: 38, 103].