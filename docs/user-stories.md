# User Stories - DistributionColisRealTime

## Table des matières

1. [User Stories Client](#user-stories-client)
2. [User Stories Livreur](#user-stories-livreur)
3. [User Stories Gestionnaire](#user-stories-gestionnaire)
4. [User Stories Administrateur](#user-stories-administrateur)

## User Stories Client

### 1. Création d'une demande de livraison

**En tant que** Client,  
**je veux** créer une demande de livraison pour un colis,  
**afin de** l'envoyer à un destinataire.

#### Préconditions
- Le client est connecté à son compte
- Le client a accès au formulaire de création de colis

#### Étapes détaillées

**Frontend** :
1. Le client accède à la page "Nouvelle livraison"
2. Le formulaire s'affiche avec les champs nécessaires :
   - Informations expéditeur (pré-remplies avec les infos du compte)
   - Informations destinataire (nom, téléphone, adresse)
   - Informations colis (poids, dimensions, contenu, valeur déclarée)
   - Type de service (standard, express)
   - Options supplémentaires (assurance, signature requise)
3. Lorsque le client modifie les informations (poids, zone, type de service), le prix est calculé dynamiquement via une API REST
4. Le client soumet le formulaire

**Backend** :
1. L'API reçoit la requête POST /api/orders
2. Le service valide les données
3. Le service calcule le prix final selon les règles de tarification
4. Un nouveau colis est créé en base de données avec un numéro de suivi unique
5. Une nouvelle commande est créée et associée au colis
6. Un événement COLIS_CREE est publié sur Kafka
7. La réponse avec les détails de la commande et le numéro de suivi est retournée

**Kafka** :
1. L'événement COLIS_CREE est consommé par NotificationConsumer
2. Une notification de confirmation est envoyée au client par email

#### Résultat attendu
- Le client reçoit une confirmation avec le numéro de suivi
- La commande apparaît dans l'historique des commandes du client
- Le colis est visible dans le système avec le statut "BORNE"

#### Règles métier
- Le poids minimum est de 0.1kg
- Les dimensions ne peuvent pas être négatives
- La valeur déclarée ne peut pas être négative
- Le service express coûte 50% plus cher que le service standard
- L'assurance est obligatoire pour les colis d'une valeur supérieure à 100€
- La signature est obligatoire pour les colis d'une valeur supérieure à 200€

---

### 2. Suivi d'un colis

**En tant que** Client,  
**je veux** suivre l'état d'envoi de mon colis,  
**afin de** savoir quand il sera livré.

#### Préconditions
- Le client a un numéro de suivi valide
- Le client est soit connecté à son compte, soit utilise la page publique de suivi

#### Étapes détaillées

**Frontend** :
1. Le client accède à la page de suivi (soit via son espace personnel, soit via la page publique)
2. Le client saisit le numéro de suivi (si nécessaire)
3. La page affiche les informations du colis et la timeline des événements

**Backend** :
1. L'API reçoit la requête GET /api/parcels/tracking/{trackingNumber}
2. Le service récupère les informations du colis en base de données
3. Le service récupère l'historique des statuts du colis
4. Les détails du colis et sa timeline sont retournés

#### Résultat attendu
- Le client voit les informations actuelles du colis (expéditeur, destinataire, service)
- Le client voit la timeline complète des événements (création, assignation, en cours de livraison, etc.)
- Si le colis est en cours de livraison, la position estimée ou le temps restant est affiché

#### Règles métier
- Le suivi est disponible pour tous les colis, qu'ils soient payés ou non
- La timeline affiche tous les changements de statut avec date et heure
- La position du livreur n'est affichée que si le colis est en cours de livraison
- Les informations confidentielles (adresse complète du destinataire) ne sont pas affichées sur la page publique

---

## User Stories Livreur

### 1. Consultation de sa tournée

**En tant que** Livreur,  
**je veux** consulter ma tournée du jour,  
**afin de** connaître les colis que je dois livrer.

#### Préconditions
- Le livreur est connecté à son compte
- Le livreur a une tournée assignée pour le jour en cours

#### Étapes détaillées

**Frontend** :
1. Le livreur accède à son tableau de bord
2. La liste des tournées du jour s'affiche
3. Le livreur sélectionne une tournée
4. La liste des colis à livrer s'affiche dans l'ordre optimisé

**Backend** :
1. L'API reçoit la requête GET /api/routes/courier/{courierId}/date/{date}
2. Le service récupère les tournées du livreur pour la date spécifiée
3. Pour chaque tournée, le service récupère les tâches associées (colis à livrer)
4. Les détails des tournées et des colis sont retournés

#### Résultat attendu
- Le livreur voit sa tournée avec tous les colis à livrer
- Les colis sont affichés dans l'ordre optimisé
- Pour chaque colis, le livreur voit : l'adresse, le nom du destinataire, le numéro de suivi, et le statut actuel

#### Règles métier
- Le livreur ne voit que ses propres tournées
- Les tournées sont affichées par ordre chronologique
- Les colis sont affichés dans l'ordre de livraison optimisé
- Le statut de chaque colis est mis à jour en temps réel

---

### 2. Mise à jour du statut d'un colis

**En tant que** Livreur,  
**je veux** mettre à jour le statut d'un colis,  
**afin de** informer le système et le client de l'avancement de la livraison.

#### Préconditions
- Le livreur est connecté à son compte
- Le livreur a une tournée en cours avec des colis assignés

#### Étapes détaillées

**Frontend** :
1. Le livreur sélectionne un colis dans sa tournée
2. Le livreur scanne le code-barres/QR code du colis (ou le sélectionne manuellement)
3. Le livreur choisit le nouveau statut : "En cours", "Livré", "Échec de livraison", etc.
4. Si le statut est "Échec de livraison", le livreur doit sélectionner une raison et ajouter des notes
5. Le livreur confirme la mise à jour

**Backend** :
1. L'API reçoit la requête PUT /api/routes/{routeId}/tasks/{taskId}?status={status}&notes={notes}
2. Le service met à jour le statut de la tâche de livraison
3. Le service met à jour le statut de la commande associée
4. Un événement Kafka approprié est publié (COLIS_EN_LIVRAISON, COLIS_LIVRE, ou COLIS_INCIDENT)
5. La réponse avec les détails mis à jour est retournée

**Kafka** :
1. L'événement est consommé par NotificationConsumer
2. Une notification est envoyée au client pour l'informer du changement de statut

#### Résultat attendu
- Le statut du colis est mis à jour dans le système
- Le client reçoit une notification du changement de statut
- La timeline du colis est mise à jour avec le nouvel événement

#### Règles métier
- Le livreur ne peut mettre à jour que les statuts des colis qui lui sont assignés
- Le passage de statut "Échec de livraison" nécessite obligatoirement une raison
- Un colis ne peut être marqué comme "Livré" que s'il était préalablement "En cours"
- Un incident génère automatiquement une nouvelle tentative de livraison pour le lendemain

---

## User Stories Gestionnaire

### 1. Création d'une tournée

**En tant que** Gestionnaire,  
**je veux** créer une tournée de livraison,  
**afin de** planifier les livraisons pour les livreurs.

#### Préconditions
- Le gestionnaire est connecté à son compte
- Le gestionnaire a les permissions de créer des tournées

#### Étapes détaillées

**Frontend** :
1. Le gestionnaire accède à la section "Tournées"
2. Le gestionnaire clique sur "Créer une nouvelle tournée"
3. Le formulaire s'affiche avec les champs nécessaires :
   - Date de la tournée
   - Zone de livraison (liste déroulante)
   - Livreur (liste déroulante filtrée par disponibilité)
   - Heure de début et de fin estimées
   - Notes optionnelles
4. Le gestionnaire remplit le formulaire et le soumet

**Backend** :
1. L'API reçoit la requête POST /api/routes
2. Le service valide les données
3. Le service vérifie que le livreur n'a pas déjà une tournée pour cette date
4. Une nouvelle tournée est créée en base de données
5. La réponse avec les détails de la tournée est retournée

#### Résultat attendu
- La nouvelle tournée est créée dans le système
- Le gestionnaire peut maintenant ajouter des colis à cette tournée
- Le livreur peut voir cette tournée dans son tableau de bord

#### Règles métier
- Un livreur ne peut avoir qu'une seule tournée par jour
- La date de la tournée ne peut pas être antérieure à la date actuelle
- L'heure de fin doit être postérieure à l'heure de début
- La zone de livraison doit correspondre aux zones définies dans le système

---

### 2. Assignation des colis aux tournées

**En tant que** Gestionnaire,  
**je veux** assigner des colis à une tournée,  
**afin de** les planifier pour la livraison.

#### Préconditions
- Le gestionnaire est connecté à son compte
- Le gestionnaire a les permissions d'assigner des colis
- Une tournée existe déjà

#### Étapes détaillées

**Frontend** :
1. Le gestionnaire accède à une tournée existante
2. Le gestionnaire clique sur "Ajouter des colis"
3. Une liste des colis non assignés s'affiche, filtrée par zone si applicable
4. Le gestionnaire sélectionne un ou plusieurs colis
5. Le gestionnaire peut définir l'ordre de livraison (ou utiliser l'optimisation automatique)
6. Le gestionnaire confirme l'assignation

**Backend** :
1. L'API reçoit la requête POST /api/routes/{id}/tasks?orderId={orderId}&taskOrder={taskOrder}
2. Le service vérifie que le colis n'est pas déjà assigné
3. Le service crée une nouvelle tâche de livraison associée à la tournée
4. Le service met à jour le statut de la commande à "ASSIGNEE"
5. Un événement COLIS_ASSIGNE est publié sur Kafka
6. La réponse avec les détails de la tournée mis à jour est retournée

**Kafka** :
1. L'événement COLIS_ASSIGNE est consommé par NotificationConsumer
2. Une notification est envoyée au client pour l'informer que son colis a été assigné

#### Résultat attendu
- Les colis sont assignés à la tournée
- Le statut des commandes est mis à jour
- Les livreurs peuvent voir les nouveaux colis dans leur tournée
- Les clients reçoivent une notification d'assignation

#### Règles métier
- Seuls les colis avec le statut "VALIDEE" ou "PAYEE" peuvent être assignés
- L'ordre de livraison doit être unique au sein d'une tournée
- Un colis ne peut être assigné qu'à une seule tournée à la fois
- L'optimisation automatique trie les livraisons par code postal pour minimiser les distances

---

## User Stories Administrateur

### 1. Gestion des utilisateurs

**En tant que** Administrateur,  
**je veux** gérer les comptes utilisateurs,  
**afin de** contrôler l'accès au système et les rôles.

#### Préconditions
- L'administrateur est connecté à son compte
- L'administrateur a les permissions de gestion des utilisateurs

#### Étapes détaillées

**Frontend** :
1. L'administrateur accède à la section "Utilisateurs"
2. Une liste de tous les utilisateurs s'affiche avec leurs rôles et statuts
3. L'administrateur peut :
   - Créer un nouvel utilisateur (bouton "Ajouter un utilisateur")
   - Modifier un utilisateur existant (clic sur une ligne)
   - Activer/désactiver un compte (bouton toggle)
   - Supprimer un utilisateur (bouton supprimer)
   - Modifier les rôles d'un utilisateur

**Backend** :
1. Pour la création : POST /api/admin/users
2. Pour la modification : PUT /api/admin/users/{id}
3. Pour la suppression : DELETE /api/admin/users/{id}
4. Pour l'activation/désactivation : PUT /api/admin/users/{id}/status

#### Résultat attendu
- Les comptes utilisateurs sont créés, modifiés ou supprimés
- Les rôles sont correctement assignés
- Les utilisateurs peuvent (ou non) se connecter selon leur statut

#### Règles métier
- L'email doit être unique pour tous les utilisateurs
- Le mot de passe doit contenir au moins 8 caractères, dont une majuscule et un chiffre
- Un administrateur ne peut pas supprimer son propre compte
- La désactivation d'un utilisateur déconnecte immédiatement toutes ses sessions actives
- Au moins un administrateur doit toujours exister dans le système

---

### 2. Gestion des zones de livraison

**En tant que** Administrateur,  
**je veux** gérer les zones de livraison,  
**afin de** définir les zones géographiques desservies et les tarifs associés.

#### Préconditions
- L'administrateur est connecté à son compte
- L'administrateur a les permissions de gestion des zones

#### Étapes détaillées

**Frontend** :
1. L'administrateur accède à la section "Zones de livraison"
2. Une liste de toutes les zones s'affiche
3. L'administrateur peut :
   - Créer une nouvelle zone (bouton "Ajouter une zone")
   - Modifier une zone existante (clic sur une ligne)
   - Activer/désactiver une zone (bouton toggle)
   - Supprimer une zone (bouton supprimer)
   - Définir les codes postaux pour chaque zone

**Backend** :
1. Pour la création : POST /api/admin/zones
2. Pour la modification : PUT /api/admin/zones/{id}
3. Pour la suppression : DELETE /api/admin/zones/{id}

#### Résultat attendu
- Les zones de livraison sont créées, modifiées ou supprimées
- Les codes postaux sont correctement associés aux zones
- Les tarifs peuvent être définis par zone

#### Règles métier
- Le nom de la zone doit être unique
- Une zone ne peut être supprimée si des tournées actives y sont associées
- Les codes postaux ne peuvent appartenir qu'à une seule zone
- Au moins une zone doit être active à tout moment

---

### 3. Gestion des tarifs

**En tant que** Administrateur,  
**je veux** gérer les tarifs de livraison,  
**afin de** définir les coûts selon les zones, le poids et le type de service.

#### Préconditions
- L'administrateur est connecté à son compte
- L'administrateur a les permissions de gestion des tarifs

#### Étapes détaillées

**Frontend** :
1. L'administrateur accède à la section "Tarifs"
2. Une liste de toutes les règles de tarification s'affiche
3. L'administrateur peut :
   - Créer une nouvelle règle (bouton "Ajouter un tarif")
   - Modifier une règle existante (clic sur une ligne)
   - Activer/désactiver une règle (bouton toggle)
   - Supprimer une règle (bouton supprimer)

**Backend** :
1. Pour la création : POST /api/admin/pricing
2. Pour la modification : PUT /api/admin/pricing/{id}
3. Pour la suppression : DELETE /api/admin/pricing/{id}

#### Résultat attendu
- Les règles de tarification sont créées, modifiées ou supprimées
- Les prix sont correctement appliqués lors de la création de commandes
- Les tarifs peuvent varier selon la zone, le poids et le type de service

#### Règles métier
- Le prix de base ne peut pas être négatif
- Le prix par kg ne peut pas être négatif
- Une règle inactive n'est pas utilisée pour le calcul des tarifs
- Si plusieurs règles s'appliquent, la plus spécifique (zone + type de service) est prioritaire
