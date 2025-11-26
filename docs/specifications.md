# Spécifications Techniques - DistributionColisRealTime

## Table des matières

1. [Vue d'ensemble du système](#vue-densemble-du-système)
2. [Rôles et permissions](#rôles-et-permissions)
3. [Modules fonctionnels détaillés](#modules-fonctionnels-détaillés)
4. [Architecture technique](#architecture-technique)
5. [Modèle de données](#modèle-de-données)
6. [API REST](#api-rest)
7. [Événements Kafka](#événements-kafka)
8. [Scénarios détaillés](#scénarios-détaillés)
9. [Frontend](#frontend)
10. [Backend](#backend)
11. [Infrastructure Docker](#infrastructure-docker)
12. [Tests et bonnes pratiques](#tests-et-bonnes-pratiques)

## Vue d'ensemble du système

DistributionColisRealTime est une plateforme de gestion de colis en temps réel qui permet aux clients de créer des envois, de suivre leur progression, et aux livreurs de gérer leurs tournées efficacement. Le système offre une interface complète pour les gestionnaires et les administrateurs afin de superviser l'ensemble des opérations logistiques.

## Rôles et permissions

### Tableau des rôles et permissions

| Rôle | Client | Livreur | Gestionnaire | Administrateur |
|------|--------|---------|--------------|----------------|
| Créer un compte | ✅ | ❌ | ❌ | ❌ |
| Se connecter | ✅ | ✅ | ✅ | ✅ |
| Créer une demande de livraison | ✅ | ❌ | ✅ | ✅ |
| Payer une commande | ✅ | ❌ | ❌ | ❌ |
| Suivre l'état de ses colis | ✅ | ❌ | ✅ | ✅ |
| Modifier/annuler une demande avant prise en charge | ✅ | ❌ | ✅ | ✅ |
| Voir l'historique des livraisons | ✅ | ❌ | ✅ | ✅ |
| Voir les tournées du jour | ❌ | ✅ | ✅ | ✅ |
| Voir les colis associés à une tournée | ❌ | ✅ | ✅ | ✅ |
| Changer le statut d'un colis | ❌ | ✅ | ✅ | ✅ |
| Signaler un incident | ❌ | ✅ | ✅ | ✅ |
| Gérer les comptes livreurs | ❌ | ❌ | ✅ | ✅ |
| Gérer les zones de livraison | ❌ | ❌ | ✅ | ✅ |
| Gérer les tarifs | ❌ | ❌ | ✅ | ✅ |
| Assigner les colis aux livreurs | ❌ | ❌ | ✅ | ✅ |
| Consulter les tableaux de bord | ❌ | ❌ | ✅ | ✅ |
| Gérer les rôles utilisateurs | ❌ | ❌ | ❌ | ✅ |
| Paramétrages globaux | ❌ | ❌ | ❌ | ✅ |

## Modules fonctionnels détaillés

### Module Authentification & Utilisateurs

#### Fonctionnalités
- Inscription client (email, téléphone, mot de passe, adresse par défaut, etc.)
- Login / logout / rafraîchissement de token
- Gestion des rôles (CLIENT, LIVREUR, GESTIONNAIRE, ADMIN)
- Profil utilisateur (modification mot de passe, coordonnées, etc.)

#### Entités principales
- User: informations de base de l'utilisateur
- Role: définition des rôles système
- UserRole: association entre utilisateur et rôle
- Profile: informations détaillées de l'utilisateur

### Module Colis & Commandes

#### Fonctionnalités
- Création d'une commande de livraison
- Calcul automatique du prix
- Génération d'un numéro de suivi unique
- Suivi des statuts de la commande

#### Entités principales
- Parcel: informations sur le colis (poids, dimensions, contenu)
- ShipmentOrder: commande de livraison (expéditeur, destinataire, colis)
- OrderStatus: statuts possibles d'une commande (BORNE, VALIDEE, PAYEE, etc.)
- PricingRule: règles de tarification

### Module Planification & Tournées

#### Fonctionnalités
- Création de tournées de livraison
- Association de colis à une tournée
- Optimisation simple de l'ordre des livraisons

#### Entités principales
- DeliveryRoute: tournée de livraison (date, zone, livreur)
- DeliveryTask: tâche de livraison individuelle (colis, ordre dans la tournée)
- DeliveryZone: zone géographique de livraison

### Module Suivi & Tracking

#### Fonctionnalités
- Mise à jour des statuts des colis par le livreur
- Historique des statuts d'un colis
- Page de suivi pour le client

#### Entités principales
- ParcelStatus: statut actuel d'un colis
- ParcelStatusHistory: historique des changements de statut
- TrackingEvent: événement de suivi (date, lieu, description)

### Module Notifications

#### Fonctionnalités
- Publication d'événements Kafka
- Consommation des événements pour envoi de notifications
- Gestion des préférences de notification

#### Entités principales
- Notification: notification envoyée à un utilisateur
- NotificationPreference: préférences de notification par utilisateur
- KafkaEvent: événement système publié

### Module Rapports / Dashboard

#### Fonctionnalités
- Statistiques sur les volumes de colis
- Taux de réussite des livraisons
- Zones avec incidents fréquents
- Performance des livreurs

#### Entités principales
- DeliveryReport: rapport de livraison
- IncidentReport: rapport d'incident
- PerformanceMetrics: métriques de performance

## Architecture technique

### Choix d'architecture

Nous adoptons une architecture microservices pour les raisons suivantes :
- **Scalabilité** : chaque service peut être scalé indépendamment selon la charge
- **Maintenance** : les services plus petits sont plus faciles à comprendre et maintenir
- **Déploiement** : possibilité de déployer chaque service indépendamment
- **Technologie** : flexibilité d'utiliser différentes technologies pour différents services

### Services principaux

1. **Gateway Service** (Porte d'entrée API)
   - Route les requêtes vers les services appropriés
   - Gère l'authentification centralisée
   - Implémente le rate limiting

2. **Auth Service** (Gestion des utilisateurs et de l'authentification)
   - Gestion des comptes utilisateurs
   - Authentification et génération de JWT
   - Gestion des rôles et permissions

3. **Parcel Service** (Gestion des colis et commandes)
   - CRUD sur les colis et commandes
   - Calcul des tarifs
   - Gestion des statuts

4. **Routing Service** (Planification et tournées)
   - Création des tournées
   - Assignation des colis aux livreurs
   - Optimisation des routes

5. **Notification Service** (Gestion des notifications)
   - Consommation des événements Kafka
   - Envoi d'emails/SMS
   - Gestion des préférences

### Communication entre services

- **Communication synchrone** : REST API via Gateway
- **Communication asynchrone** : Apache Kafka pour les événements
- **Découverte de services** : Eureka Server (optionnel pour début)

## Modèle de données

### Schéma de la base de données

```sql
-- Tables d'utilisateurs et rôles
CREATE TABLE role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE user (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE user_role (
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE user_profile (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    profile_picture_url VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Tables de gestion des colis et commandes
CREATE TABLE delivery_zone (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    postal_codes TEXT[], -- Array de codes postaux
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE pricing_rule (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    price_per_kg DECIMAL(10,2) NOT NULL,
    zone_id INTEGER,
    service_type VARCHAR(50), -- STANDARD, EXPRESS
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (zone_id) REFERENCES delivery_zone(id)
);

CREATE TABLE parcel (
    id SERIAL PRIMARY KEY,
    weight DECIMAL(10,2) NOT NULL,
    length DECIMAL(10,2),
    width DECIMAL(10,2),
    height DECIMAL(10,2),
    content_description TEXT,
    declared_value DECIMAL(10,2),
    tracking_number VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE shipment_order (
    id SERIAL PRIMARY KEY,
    parcel_id INTEGER NOT NULL,
    sender_id INTEGER NOT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    recipient_phone VARCHAR(20),
    recipient_address_line1 VARCHAR(255) NOT NULL,
    recipient_address_line2 VARCHAR(255),
    recipient_city VARCHAR(100) NOT NULL,
    recipient_postal_code VARCHAR(20) NOT NULL,
    recipient_country VARCHAR(100),
    service_type VARCHAR(50) NOT NULL, -- STANDARD, EXPRESS
    status VARCHAR(50) NOT NULL DEFAULT 'BORNE', -- BORNE, VALIDEE, PAYEE, ASSIGNEE, EN_COURS, LIVREE, ANNULEE, RETOUR
    price DECIMAL(10,2) NOT NULL,
    insurance BOOLEAN DEFAULT FALSE,
    signature_required BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parcel_id) REFERENCES parcel(id),
    FOREIGN KEY (sender_id) REFERENCES user(id)
);

-- Tables de planification et tournées
CREATE TABLE delivery_route (
    id SERIAL PRIMARY KEY,
    courier_id INTEGER NOT NULL,
    zone_id INTEGER,
    route_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PLANNED', -- PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
    start_time TIME,
    end_time TIME,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (courier_id) REFERENCES user(id),
    FOREIGN KEY (zone_id) REFERENCES delivery_zone(id)
);

CREATE TABLE delivery_task (
    id SERIAL PRIMARY KEY,
    route_id INTEGER NOT NULL,
    order_id INTEGER NOT NULL,
    task_order INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, IN_PROGRESS, COMPLETED, FAILED
    planned_arrival_time TIME,
    actual_arrival_time TIME,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES delivery_route(id),
    FOREIGN KEY (order_id) REFERENCES shipment_order(id)
);

-- Tables de suivi et notifications
CREATE TABLE parcel_status (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    location VARCHAR(255),
    description TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES shipment_order(id)
);

CREATE TABLE notification (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL, -- EMAIL, SMS, PUSH
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, SENT, FAILED
    sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE notification_preference (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE,
    email_enabled BOOLEAN DEFAULT TRUE,
    sms_enabled BOOLEAN DEFAULT FALSE,
    push_enabled BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Tables de rapports
CREATE TABLE delivery_incident (
    id SERIAL PRIMARY KEY,
    task_id INTEGER NOT NULL,
    incident_type VARCHAR(50) NOT NULL, -- CUSTOMER_ABSENT, ADDRESS_NOT_FOUND, DAMAGED_PARCEL, REFUSED_DELIVERY
    description TEXT,
    resolved BOOLEAN DEFAULT FALSE,
    resolution_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES delivery_task(id)
);
```

## API REST

### Authentification Service

| Méthode | URL | Rôle requis | Description |
|---------|-----|-------------|-------------|
| POST | /api/auth/register | Public | Inscription d'un nouvel utilisateur |
| POST | /api/auth/login | Public | Connexion d'un utilisateur |
| POST | /api/auth/refresh | Public | Rafraîchissement du token JWT |
| POST | /api/auth/logout | Authentifié | Déconnexion |
| GET | /api/auth/profile | Authentifié | Récupération du profil utilisateur |
| PUT | /api/auth/profile | Authentifié | Mise à jour du profil utilisateur |
| POST | /api/auth/change-password | Authentifié | Changement du mot de passe |

### Parcel Service

| Méthode | URL | Rôle requis | Description |
|---------|-----|-------------|-------------|
| POST | /api/parcels | CLIENT, GESTIONNAIRE, ADMIN | Création d'un nouveau colis |
| GET | /api/parcels | Authentifié | Liste des colis (filtrée selon le rôle) |
| GET | /api/parcels/{id} | Authentifié | Détails d'un colis |
| PUT | /api/parcels/{id} | CLIENT, GESTIONNAIRE, ADMIN | Mise à jour d'un colis |
| DELETE | /api/parcels/{id} | CLIENT, GESTIONNAIRE, ADMIN | Suppression d'un colis |
| GET | /api/parcels/tracking/{trackingNumber} | Public | Suivi d'un colis par numéro de suivi |

### Shipment Order Service

| Méthode | URL | Rôle requis | Description |
|---------|-----|-------------|-------------|
| POST | /api/orders | CLIENT, GESTIONNAIRE, ADMIN | Création d'une nouvelle commande |
| GET | /api/orders | Authentifié | Liste des commandes (filtrée selon le rôle) |
| GET | /api/orders/{id} | Authentifié | Détails d'une commande |
| PUT | /api/orders/{id} | CLIENT, GESTIONNAIRE, ADMIN | Mise à jour d'une commande |
| DELETE | /api/orders/{id} | CLIENT, GESTIONNAIRE, ADMIN | Annulation d'une commande |
| POST | /api/orders/{id}/pay | CLIENT | Paiement d'une commande |
| GET | /api/orders/{id}/status | Authentifié | Statut d'une commande |

### Routing Service

| Méthode | URL | Rôle requis | Description |
|---------|-----|-------------|-------------|
| POST | /api/routes | GESTIONNAIRE, ADMIN | Création d'une nouvelle tournée |
| GET | /api/routes | Authentifié | Liste des tournées (filtrée selon le rôle) |
| GET | /api/routes/{id} | Authentifié | Détails d'une tournée |
| PUT | /api/routes/{id} | GESTIONNAIRE, ADMIN | Mise à jour d'une tournée |
| DELETE | /api/routes/{id} | GESTIONNAIRE, ADMIN | Suppression d'une tournée |
| POST | /api/routes/{id}/optimize | GESTIONNAIRE, ADMIN | Optimisation d'une tournée |
| POST | /api/routes/{id}/tasks | GESTIONNAIRE, ADMIN | Ajout d'une tâche à une tournée |
| PUT | /api/routes/{routeId}/tasks/{taskId} | LIVREUR, GESTIONNAIRE, ADMIN | Mise à jour d'une tâche |
| GET | /api/routes/courier/{courierId}/date/{date} | LIVREUR, GESTIONNAIRE, ADMIN | Tournées d'un livreur pour une date |

### Notification Service

| Méthode | URL | Rôle requis | Description |
|---------|-----|-------------|-------------|
| GET | /api/notifications | Authentifié | Liste des notifications |
| PUT | /api/notifications/{id}/read | Authentifié | Marquer une notification comme lue |
| GET | /api/notifications/preferences | Authentifié | Préférences de notification |
| PUT | /api/notifications/preferences | Authentifié | Mise à jour des préférences |

### Admin Service

| Méthode | URL | Rôle requis | Description |
|---------|-----|-------------|-------------|
| GET | /api/admin/users | ADMIN | Liste des utilisateurs |
| POST | /api/admin/users | ADMIN | Création d'un utilisateur |
| GET | /api/admin/users/{id} | ADMIN | Détails d'un utilisateur |
| PUT | /api/admin/users/{id} | ADMIN | Mise à jour d'un utilisateur |
| DELETE | /api/admin/users/{id} | ADMIN | Suppression d'un utilisateur |
| GET | /api/admin/zones | ADMIN, GESTIONNAIRE | Liste des zones de livraison |
| POST | /api/admin/zones | ADMIN, GESTIONNAIRE | Création d'une zone de livraison |
| PUT | /api/admin/zones/{id} | ADMIN, GESTIONNAIRE | Mise à jour d'une zone de livraison |
| DELETE | /api/admin/zones/{id} | ADMIN, GESTIONNAIRE | Suppression d'une zone de livraison |
| GET | /api/admin/pricing | ADMIN, GESTIONNAIRE | Liste des règles de tarification |
| POST | /api/admin/pricing | ADMIN, GESTIONNAIRE | Création d'une règle de tarification |
| PUT | /api/admin/pricing/{id} | ADMIN, GESTIONNAIRE | Mise à jour d'une règle de tarification |
| DELETE | /api/admin/pricing/{id} | ADMIN, GESTIONNAIRE | Suppression d'une règle de tarification |
| GET | /api/admin/reports/delivery | ADMIN, GESTIONNAIRE | Rapport de livraisons |
| GET | /api/admin/reports/incidents | ADMIN, GESTIONNAIRE | Rapport d'incidents |
| GET | /api/admin/reports/performance | ADMIN, GESTIONNAIRE | Rapport de performance |

## Événements Kafka

### Topics et schémas

#### parcel-events
Événements liés au cycle de vie des colis

```json
// COLIS_CREE
{
  "eventId": "uuid",
  "eventType": "COLIS_CREE",
  "timestamp": "2023-06-15T10:30:00Z",
  "data": {
    "parcelId": 123,
    "trackingNumber": "TRK123456789",
    "senderId": 456,
    "recipientInfo": {
      "name": "Jean Dupont",
      "address": "123 rue de la Paix",
      "city": "Paris",
      "postalCode": "75001"
    },
    "serviceType": "STANDARD"
  }
}

// COLIS_ASSIGNE
{
  "eventId": "uuid",
  "eventType": "COLIS_ASSIGNE",
  "timestamp": "2023-06-15T11:00:00Z",
  "data": {
    "parcelId": 123,
    "trackingNumber": "TRK123456789",
    "routeId": 789,
    "courierId": 654,
    "estimatedDelivery": "2023-06-15T18:00:00Z"
  }
}

// COLIS_EN_LIVRAISON
{
  "eventId": "uuid",
  "eventType": "COLIS_EN_LIVRAISON",
  "timestamp": "2023-06-15T17:30:00Z",
  "data": {
    "parcelId": 123,
    "trackingNumber": "TRK123456789",
    "courierId": 654,
    "location": "Paris, 75001"
  }
}

// COLIS_LIVRE
{
  "eventId": "uuid",
  "eventType": "COLIS_LIVRE",
  "timestamp": "2023-06-15T17:45:00Z",
  "data": {
    "parcelId": 123,
    "trackingNumber": "TRK123456789",
    "courierId": 654,
    "deliveryTime": "2023-06-15T17:45:00Z",
    "recipientName": "Jean Dupont",
    "signature": "base64-encoded-signature"
  }
}

// COLIS_INCIDENT
{
  "eventId": "uuid",
  "eventType": "COLIS_INCIDENT",
  "timestamp": "2023-06-15T17:30:00Z",
  "data": {
    "parcelId": 123,
    "trackingNumber": "TRK123456789",
    "courierId": 654,
    "incidentType": "CUSTOMER_ABSENT",
    "description": "Client absent au moment de la livraison",
    "location": "Paris, 75001",
    "nextAttempt": "2023-06-16T10:00:00Z"
  }
}
```

#### user-events
Événements liés aux utilisateurs

```json
// USER_REGISTERED
{
  "eventId": "uuid",
  "eventType": "USER_REGISTERED",
  "timestamp": "2023-06-15T09:00:00Z",
  "data": {
    "userId": 456,
    "email": "client@example.com",
    "role": "CLIENT",
    "registrationDate": "2023-06-15T09:00:00Z"
  }
}

// USER_LOGIN
{
  "eventId": "uuid",
  "eventType": "USER_LOGIN",
  "timestamp": "2023-06-15T10:15:00Z",
  "data": {
    "userId": 456,
    "email": "client@example.com",
    "loginTime": "2023-06-15T10:15:00Z",
    "ipAddress": "192.168.1.100"
  }
}
```

#### route-events
Événements liés aux tournées

```json
// ROUTE_CREATED
{
  "eventId": "uuid",
  "eventType": "ROUTE_CREATED",
  "timestamp": "2023-06-15T08:00:00Z",
  "data": {
    "routeId": 789,
    "courierId": 654,
    "routeDate": "2023-06-15",
    "zone": "Paris Centre",
    "estimatedStartTime": "2023-06-15T09:00:00Z",
    "estimatedEndTime": "2023-06-15T18:00:00Z",
    "parcelCount": 15
  }
}

// ROUTE_COMPLETED
{
  "eventId": "uuid",
  "eventType": "ROUTE_COMPLETED",
  "timestamp": "2023-06-15T18:15:00Z",
  "data": {
    "routeId": 789,
    "courierId": 654,
    "completionTime": "2023-06-15T18:15:00Z",
    "deliveredCount": 13,
    "failedCount": 2
  }
}
```

### Consommateurs Kafka

1. **NotificationConsumer**
   - Consomme les événements de parcel-events
   - Génère des notifications appropriées (email, SMS, push)
   - Stocke les notifications en base de données

2. **TrackingConsumer**
   - Consomme les événements de parcel-events
   - Met à jour l'historique de suivi des colis
   - Agrège les données pour le dashboard de suivi

3. **AnalyticsConsumer**
   - Consomme tous les types d'événements
   - Agrège les données pour les rapports et tableaux de bord
   - Stocke les métriques dans une base de données optimisée pour l'analyse

## Scénarios détaillés

### Client - Création d'une demande de livraison

**User Story** : En tant que client, je veux créer une demande de livraison pour un colis, afin de l'envoyer à un destinataire.

**Préconditions** :
- Le client est connecté à son compte
- Le client a accès au formulaire de création de colis

**Étapes détaillées** :

1. **Frontend** :
   - Le client accède à la page "Nouvelle livraison"
   - Le formulaire s'affiche avec les champs nécessaires :
     - Informations expéditeur (pré-remplies avec les infos du compte)
     - Informations destinataire (nom, téléphone, adresse)
     - Informations colis (poids, dimensions, contenu, valeur déclarée)
     - Type de service (standard, express)
     - Options supplémentaires (assurance, signature requise)
   - Lorsque le client modifie les informations (poids, zone, type de service), le prix est calculé dynamiquement
   - Le client soumet le formulaire

2. **Backend** :
   - L'API reçoit la requête POST /api/orders
   - Le service valide les données
   - Le service calcule le prix final selon les règles de tarification
   - Un nouveau colis est créé en base de données avec un numéro de suivi unique
   - Une nouvelle commande est créée et associée au colis
   - Un événement COLIS_CREE est publié sur Kafka
   - La réponse avec les détails de la commande et le numéro de suivi est retournée

3. **Kafka** :
   - L'événement COLIS_CREE est consommé par NotificationConsumer
   - Une notification de confirmation est envoyée au client par email

**Résultat attendu** :
- Le client reçoit une confirmation avec le numéro de suivi
- La commande apparaît dans l'historique des commandes du client
- Le colis est visible dans le système avec le statut "BORNE"

### Client - Suivi d'un colis

**User Story** : En tant que client, je veux suivre l'état d'envoi de mon colis, afin de savoir quand il sera livré.

**Préconditions** :
- Le client a un numéro de suivi valide
- Le client est soit connecté à son compte, soit utilise la page publique de suivi

**Étapes détaillées** :

1. **Frontend** :
   - Le client accède à la page de suivi (soit via son espace personnel, soit via la page publique)
   - Le client saisit le numéro de suivi (si nécessaire)
   - La page affiche les informations du colis et la timeline des événements

2. **Backend** :
   - L'API reçoit la requête GET /api/parcels/tracking/{trackingNumber}
   - Le service récupère les informations du colis en base de données
   - Le service récupère l'historique des statuts du colis
   - Les détails du colis et sa timeline sont retournés

**Résultat attendu** :
- Le client voit les informations actuelles du colis (expéditeur, destinataire, service)
- Le client voit la timeline complète des événements (création, assignation, en cours de livraison, etc.)
- Si le colis est en cours de livraison, la position estimée ou le temps restant est affiché

### Livreur - Consultation de sa tournée

**User Story** : En tant que livreur, je veux consulter ma tournée du jour, afin de connaître les colis que je dois livrer.

**Préconditions** :
- Le livreur est connecté à son compte
- Le livreur a une tournée assignée pour le jour en cours

**Étapes détaillées** :

1. **Frontend** :
   - Le livreur accède à son tableau de bord
   - La liste des tournées du jour s'affiche
   - Le livreur sélectionne une tournée
   - La liste des colis à livrer s'affiche dans l'ordre optimisé

2. **Backend** :
   - L'API reçoit la requête GET /api/routes/courier/{courierId}/date/{date}
   - Le service récupère les tournées du livreur pour la date spécifiée
   - Pour chaque tournée, le service récupère les tâches associées (colis à livrer)
   - Les détails des tournées et des colis sont retournés

**Résultat attendu** :
- Le livreur voit sa tournée du jour avec les informations clés (heure de début, zone, nombre de colis)
- Le livreur voit la liste détaillée des colis à livrer dans l'ordre optimisé
- Pour chaque colis, le livreur voit les informations nécessaires (adresse, nom du destinataire, instructions spéciales)

### Livreur - Mise à jour du statut d'un colis

**User Story** : En tant que livreur, je veux mettre à jour le statut d'un colis que je livre, afin d'informer le système de sa progression.

**Préconditions** :
- Le livreur est connecté à son compte
- Le livreur a accès à sa tournée en cours

**Étapes détaillées** :

1. **Frontend** :
   - Le livreur sélectionne un colis dans sa tournée
   - Le livreur choisit une action (commencer la livraison, marquer comme livré, signaler un incident)
   - Si nécessaire, le livreur ajoute des informations complémentaires (motif de l'incident, photo, signature)
   - Le livreur soumet la mise à jour

2. **Backend** :
   - L'API reçoit la requête PUT /api/routes/{routeId}/tasks/{taskId}
   - Le service valide les données
   - Le service met à jour le statut de la tâche de livraison
   - Le service met à jour le statut du colis
   - Un événement approprié est publié sur Kafka (COLIS_EN_LIVRAISON, COLIS_LIVRE, COLIS_INCIDENT)
   - La réponse confirmant la mise à jour est retournée

3. **Kafka** :
   - L'événement est consommé par NotificationConsumer
   - Une notification est envoyée au client concerné
   - L'événement est consommé par TrackingConsumer pour mettre à jour l'historique de suivi

**Résultat attendu** :
- Le statut du colis est mis à jour dans le système
- Le client reçoit une notification du changement de statut
- La timeline de suivi du colis est mise à jour

### Gestionnaire - Création d'une tournée

**User Story** : En tant que gestionnaire, je veux créer une tournée de livraison, afin d'assigner des colis à un livreur pour une journée.

**Préconditions** :
- Le gestionnaire est connecté à son compte
- Le gestionnaire a accès à l'interface de gestion des tournées

**Étapes détaillées** :

1. **Frontend** :
   - Le gestionnaire accède à la page de gestion des tournées
   - Le gestionnaire clique sur "Créer une nouvelle tournée"
   - Le formulaire s'affiche avec les champs nécessaires (date, zone, livreur)
   - Le gestionnaire sélectionne les colis non assignés pour la zone
   - Le gestionnaire soumet le formulaire

2. **Backend** :
   - L'API reçoit la requête POST /api/routes
   - Le service valide les données
   - Une nouvelle tournée est créée en base de données
   - Les colis sélectionnés sont associés à la tournée
   - Un événement ROUTE_CREATED est publié sur Kafka
   - La réponse avec les détails de la tournée est retournée

3. **Kafka** :
   - L'événement ROUTE_CREATED est consommé par NotificationConsumer
   - Une notification est envoyée au livreur assigné à la tournée

**Résultat attendu** :
- La nouvelle tournée apparaît dans la liste des tournées
- Les colis assignés ont leur statut mis à jour à "ASSIGNEE"
- Le livreur reçoit une notification de sa nouvelle tournée

### Administrateur - Gestion des utilisateurs

**User Story** : En tant qu'administrateur, je veux gérer les comptes utilisateurs, afin de contrôler l'accès au système.

**Préconditions** :
- L'administrateur est connecté à son compte
- L'administrateur a accès à l'interface d'administration

**Étapes détaillées** :

1. **Frontend** :
   - L'administrateur accède à la page de gestion des utilisateurs
   - L'administrateur peut voir la liste des utilisateurs existants
   - L'administrateur peut créer un nouvel utilisateur
   - L'administrateur peut modifier les informations d'un utilisateur existant
   - L'administrateur peut activer/désactiver un compte

2. **Backend** :
   - Pour la création : L'API reçoit la requête POST /api/admin/users
   - Pour la modification : L'API reçoit la requête PUT /api/admin/users/{id}
   - Pour la suppression/désactivation : L'API reçoit la requête DELETE /api/admin/users/{id}
   - Le service effectue les opérations appropriées en base de données
   - Les réponses confirmant les opérations sont retournées

**Résultat attendu** :
- Les utilisateurs sont créés, modifiés ou désactivés selon les actions de l'administrateur
- Les utilisateurs concernés reçoivent des notifications des changements (sauf pour la désactivation)
- Les permissions d'accès au système sont mises à jour

## Frontend

### Arborescence des pages et composants

```
src/
├── assets/
│   ├── images/
│   └── styles/
├── components/
│   ├── common/
│   │   ├── TheHeader.vue
│   │   ├── TheSidebar.vue
│   │   ├── TheFooter.vue
│   │   ├── BaseButton.vue
│   │   ├── BaseInput.vue
│   │   ├── BaseModal.vue
│   │   ├── LoadingSpinner.vue
│   │   └── NotificationToast.vue
│   ├── auth/
│   │   ├── LoginForm.vue
│   │   ├── RegisterForm.vue
│   │   └── PasswordResetForm.vue
│   ├── parcel/
│   │   ├── ParcelForm.vue
│   │   ├── ParcelList.vue
│   │   ├── ParcelCard.vue
│   │   ├── ParcelStatusTimeline.vue
│   │   └── TrackingSearch.vue
│   ├── route/
│   │   ├── RouteList.vue
│   │   ├── RouteCard.vue
│   │   ├── RouteMap.vue
│   │   └── TaskList.vue
│   └── admin/
│       ├── UserForm.vue
│       ├── UserList.vue
│       ├── ZoneForm.vue
│       ├── ZoneList.vue
│       ├── PricingForm.vue
│       └── Dashboard.vue
├── layouts/
│   ├── DefaultLayout.vue
│   ├── AuthLayout.vue
│   └── AdminLayout.vue
├── pages/
│   ├── auth/
│   │   ├── Login.vue
│   │   ├── Register.vue
│   │   └── ForgotPassword.vue
│   ├── client/
│   │   ├── Dashboard.vue
│   │   ├── Parcels.vue
│   │   ├── NewParcel.vue
│   │   ├── ParcelDetail.vue
│   │   └── Profile.vue
│   ├── courier/
│   │   ├── Dashboard.vue
│   │   ├── Routes.vue
│   │   ├── RouteDetail.vue
│   │   └── Profile.vue
│   ├── admin/
│   │   ├── Dashboard.vue
│   │   ├── Users.vue
│   │   ├── Zones.vue
│   │   ├── Pricing.vue
│   │   └── Settings.vue
│   └── public/
│       └── Tracking.vue
├── router/
│   └── index.ts
├── stores/
│   ├── auth.ts
│   ├── parcel.ts
│   ├── route.ts
│   └── notification.ts
├── services/
│   ├── api.ts
│   ├── auth.service.ts
│   ├── parcel.service.ts
│   ├── route.service.ts
│   └── notification.service.ts
├── utils/
│   ├── constants.ts
│   ├── helpers.ts
│   └── validators.ts
├── App.vue
└── main.ts
```

### Pages principales

#### Pages d'authentification
- `/login` : Formulaire de connexion
- `/register` : Formulaire d'inscription
- `/forgot-password` : Formulaire de récupération de mot de passe

#### Pages client
- `/client/dashboard` : Tableau de bord du client (résumé des livraisons)
- `/client/parcels` : Liste des colis du client
- `/client/parcels/new` : Formulaire de création d'un nouveau colis
- `/client/parcels/:id` : Détails d'un colis
- `/client/profile` : Profil du client

#### Pages livreur
- `/courier/dashboard` : Tableau de bord du livreur (tournée du jour)
- `/courier/routes` : Liste des tournées du livreur
- `/courier/routes/:id` : Détails d'une tournée avec les colis à livrer
- `/courier/profile` : Profil du livreur

#### Pages administrateur
- `/admin/dashboard` : Tableau de bord administrateur (statistiques)
- `/admin/users` : Gestion des utilisateurs
- `/admin/zones` : Gestion des zones de livraison
- `/admin/pricing` : Gestion des tarifs
- `/admin/settings` : Paramètres système

#### Page publique
- `/tracking` : Page publique de suivi de colis

### État global (Pinia)

#### Store auth
```typescript
interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  role: string | null;
}
```

#### Store parcel
```typescript
interface ParcelState {
  parcels: Parcel[];
  currentParcel: Parcel | null;
  trackingResult: Parcel | null;
  loading: boolean;
  error: string | null;
}
```

#### Store route
```typescript
interface RouteState {
  routes: Route[];
  currentRoute: Route | null;
  loading: boolean;
  error: string | null;
}
```

#### Store notification
```typescript
interface NotificationState {
  notifications: Notification[];
  unreadCount: number;
  preferences: NotificationPreferences;
}
```

### Gestion des erreurs et messages

- Utilisation de toasts pour les notifications de succès/erreur
- Gestion centralisée des erreurs API dans un intercepteur Axios
- Messages d'erreur clairs et actionnables
- Indicateurs de chargement pendant les opérations asynchrones

### Gestion de l'authentification

- Stockage du token JWT dans localStorage
- Rafraîchissement automatique du token avant expiration
- Guards de route pour protéger les pages selon les rôles
- Redirection automatique vers la page de connexion si nécessaire

## Backend

### Organisation du code

```
src/main/java/com/distribution/colis/
├── config/
│   ├── SecurityConfig.java
│   ├── KafkaConfig.java
│   └── SwaggerConfig.java
├── controller/
│   ├── AuthController.java
│   ├── ParcelController.java
│   ├── ShipmentOrderController.java
│   ├── RoutingController.java
│   ├── NotificationController.java
│   └── AdminController.java
├── service/
│   ├── AuthService.java
│   ├── ParcelService.java
│   ├── ShipmentOrderService.java
│   ├── RoutingService.java
│   ├── NotificationService.java
│   ├── PricingService.java
│   └── EmailService.java
├── repository/
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── ParcelRepository.java
│   ├── ShipmentOrderRepository.java
│   ├── DeliveryRouteRepository.java
│   ├── DeliveryTaskRepository.java
│   └── NotificationRepository.java
├── model/
│   ├── entity/
│   │   ├── User.java
│   │   ├── Role.java
│   │   ├── Parcel.java
│   │   ├── ShipmentOrder.java
│   │   ├── DeliveryRoute.java
│   │   ├── DeliveryTask.java
│   │   ├── Notification.java
│   │   └── DeliveryZone.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   ├── ParcelRequest.java
│   │   │   └── ShipmentOrderRequest.java
│   │   └── response/
│   │       ├── AuthResponse.java
│   │       ├── ParcelResponse.java
│   │       └── ShipmentOrderResponse.java
│   └── enums/
│       ├── Role.java
│       ├── OrderStatus.java
│       ├── ServiceType.java
│       └── IncidentType.java
├── kafka/
│   ├── producer/
│   │   └── ParcelEventProducer.java
│   └── consumer/
│       ├── NotificationConsumer.java
│       ├── TrackingConsumer.java
│       └── AnalyticsConsumer.java
├── security/
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
└── DistributionColisApplication.java
```

### Exemples de code

#### Entité Parcel
```java
@Entity
@Table(name = "parcel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal weight;

    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;

    @Column(columnDefinition = "TEXT")
    private String contentDescription;

    private BigDecimal declaredValue;

    @Column(name = "tracking_number", nullable = false, unique = true)
    private String trackingNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "parcel", cascade = CascadeType.ALL)
    private ShipmentOrder shipmentOrder;

    @OneToMany(mappedBy = "parcel")
    private List<ParcelStatus> statusHistory;
}
```

#### Controller pour créer un colis
```java
@RestController
@RequestMapping("/api/parcels")
@Validated
public class ParcelController {

    private final ParcelService parcelService;

    public ParcelController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'GESTIONNAIRE', 'ADMIN')")
    public ResponseEntity<ParcelResponse> createParcel(
            @Valid @RequestBody ParcelRequest parcelRequest,
            Authentication authentication) {

        String email = authentication.getName();
        Parcel parcel = parcelService.createParcel(parcelRequest, email);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ParcelResponse.from(parcel));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ParcelResponse> getParcel(
            @PathVariable Long id,
            Authentication authentication) {

        Parcel parcel = parcelService.getParcel(id, authentication);

        return ResponseEntity.ok(ParcelResponse.from(parcel));
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<ParcelResponse> trackParcel(
            @PathVariable String trackingNumber) {

        Parcel parcel = parcelService.getParcelByTrackingNumber(trackingNumber);

        return ResponseEntity.ok(ParcelResponse.from(parcel));
    }
}
```

#### Publisher Kafka
```java
@Component
@Slf4j
public class ParcelEventProducer {

    private static final String TOPIC = "parcel-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ParcelEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendParcelCreatedEvent(Parcel parcel) {
        ParcelCreatedEvent event = ParcelCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("COLIS_CREE")
                .timestamp(Instant.now())
                .data(ParcelEventData.builder()
                        .parcelId(parcel.getId())
                        .trackingNumber(parcel.getTrackingNumber())
                        .senderId(parcel.getShipmentOrder().getSenderId())
                        .recipientInfo(RecipientInfo.builder()
                                .name(parcel.getShipmentOrder().getRecipientName())
                                .address(parcel.getShipmentOrder().getRecipientAddressLine1())
                                .city(parcel.getShipmentOrder().getRecipientCity())
                                .postalCode(parcel.getShipmentOrder().getRecipientPostalCode())
                                .build())
                        .serviceType(parcel.getShipmentOrder().getServiceType())
                        .build())
                .build();

        kafkaTemplate.send(TOPIC, event);
        log.info("Sent parcel created event for parcel: {}", parcel.getTrackingNumber());
    }

    public void sendParcelStatusUpdateEvent(Parcel parcel, String status, String location) {
        ParcelStatusUpdateEvent event = ParcelStatusUpdateEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("COLIS_STATUT_MIS_A_JOUR")
                .timestamp(Instant.now())
                .data(ParcelStatusUpdateData.builder()
                        .parcelId(parcel.getId())
                        .trackingNumber(parcel.getTrackingNumber())
                        .status(status)
                        .location(location)
                        .build())
                .build();

        kafkaTemplate.send(TOPIC, event);
        log.info("Sent parcel status update event for parcel: {}, status: {}", 
                parcel.getTrackingNumber(), status);
    }
}
```

#### Consumer Kafka pour les notifications
```java
@Component
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final EmailService emailService;

    public NotificationConsumer(NotificationService notificationService, EmailService emailService) {
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @KafkaListener(topics = "parcel-events", groupId = "notification-group")
    public void handleParcelEvent(ParcelEvent event) {
        log.info("Received parcel event: {}", event.getEventType());

        switch (event.getEventType()) {
            case "COLIS_CREE":
                handleParcelCreated(event);
                break;
            case "COLIS_ASSIGNE":
                handleParcelAssigned(event);
                break;
            case "COLIS_EN_LIVRAISON":
                handleParcelInDelivery(event);
                break;
            case "COLIS_LIVRE":
                handleParcelDelivered(event);
                break;
            case "COLIS_INCIDENT":
                handleParcelIncident(event);
                break;
            default:
                log.warn("Unknown parcel event type: {}", event.getEventType());
        }
    }

    private void handleParcelCreated(ParcelEvent event) {
        // Envoyer un email de confirmation au client
        emailService.sendParcelCreatedConfirmation(event.getData());

        // Créer une notification dans la base de données
        notificationService.createNotification(
                event.getData().getSenderId(),
                "Votre colis a été créé",
                "Votre colis " + event.getData().getTrackingNumber() + " a été créé avec succès.",
                NotificationType.EMAIL
        );
    }

    private void handleParcelAssigned(ParcelEvent event) {
        // Envoyer un email au client pour l'informer que son colis a été assigné
        emailService.sendParcelAssignedNotification(event.getData());

        // Créer une notification dans la base de données
        notificationService.createNotification(
                event.getData().getSenderId(),
                "Votre colis a été assigné à un livreur",
                "Votre colis " + event.getData().getTrackingNumber() + " a été assigné à un livreur pour livraison.",
                NotificationType.EMAIL
        );
    }

    // Autres méthodes de gestion d'événements...
}
```

## Infrastructure Docker

### Docker Compose

Le fichier docker-compose.yml définit les services suivants :

1. **PostgreSQL** : Base de données principale
2. **Zookeeper** : Service de coordination pour Kafka
3. **Kafka** : Broker de messages pour la communication asynchrone
4. **Backend** : Application Spring Boot
5. **Frontend** : Application Vue.js servie par Nginx

### Dockerfile Backend

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Dockerfile Frontend

```dockerfile
# Build stage
FROM node:lts-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Production stage
FROM nginx:stable-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Configuration Nginx

Un fichier de configuration nginx.conf sera ajouté pour :
- Servir le frontend
- Proxy les requêtes API vers le backend
- Gérer le cache et les en-têtes de sécurité

## Tests et bonnes pratiques

### Types de tests

1. **Tests unitaires** :
   - Tests des services métier
   - Tests des utilitaires et helpers
   - Tests des composants Vue.js isolés

2. **Tests d'intégration** :
   - Tests des repositories JPA avec Testcontainers
   - Tests des contrôleurs REST avec MockMvc
   - Tests des consumers/producers Kafka

3. **Tests end-to-end** :
   - Tests des scénarios utilisateur critiques avec Cypress ou Playwright

### Bibliothèques de test

- **Backend** : JUnit 5, Mockito, Testcontainers, Spring Boot Test
- **Frontend** : Vitest, Vue Test Utils, Cypress

### Gestion des logs

- Format structuré des logs (JSON)
- Corrélation des logs par requête (MDC)
- Niveaux de log appropriés (DEBUG, INFO, WARN, ERROR)
- Agrégation centralisée des logs (ELK Stack ou équivalent)

### Gestion des erreurs

- Exceptions métier spécifiques
- Gestionnaire global d'exceptions avec codes HTTP appropriés
- Messages d'erreur clairs et actionnables
- Validation des entrées avec Bean Validation
- Gestion des erreurs côté frontend avec messages utilisateur

### Sécurité

- Validation des entrées pour prévenir les injections
- Gestion sécurisée des mots de passe (BCrypt)
- Configuration HTTPS en production
- Politiques de sécurité des contenus (CSP)
- Protection CSRF pour les formulaires
- Rate limiting pour prévenir les abus
