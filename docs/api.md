# Documentation API - DistributionColisRealTime

## Table des matières

1. [Authentification](#authentification)
2. [Gestion des utilisateurs](#gestion-des-utilisateurs)
3. [Gestion des colis](#gestion-des-colis)
4. [Planification des tournées](#planification-des-tournées)
5. [Suivi des colis](#suivi-des-colis)
6. [Notifications](#notifications)
7. [Rapports](#rapports)

## Authentification

### Inscription

**POST** `/api/auth/register`

Inscrit un nouvel utilisateur avec le rôle CLIENT.

**Corps de la requête**
```json
{
  "email": "client@example.com",
  "password": "Password123!",
  "firstName": "Jean",
  "lastName": "Dupont",
  "phone": "+33612345678",
  "address": {
    "line1": "123 Rue de la République",
    "line2": "Appartement 4B",
    "city": "Paris",
    "postalCode": "75001",
    "country": "France"
  }
}
```

**Réponse (201)**
```json
{
  "id": 1,
  "email": "client@example.com",
  "firstName": "Jean",
  "lastName": "Dupont",
  "phone": "+33612345678",
  "roles": ["CLIENT"]
}
```

### Connexion

**POST** `/api/auth/login`

Authentifie un utilisateur et retourne un token JWT.

**Corps de la requête**
```json
{
  "email": "client@example.com",
  "password": "Password123!"
}
```

**Réponse (200)**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "email": "client@example.com",
    "firstName": "Jean",
    "lastName": "Dupont",
    "roles": ["CLIENT"]
  }
}
```

### Rafraîchissement du token

**POST** `/api/auth/refresh`

Rafraîchit un token JWT expiré.

**Corps de la requête**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Réponse (200)**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

### Déconnexion

**POST** `/api/auth/logout`

Invalide le token JWT de l'utilisateur.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Réponse (200)**
```json
{
  "message": "Déconnexion réussie"
}
```

## Gestion des utilisateurs

### Obtenir le profil utilisateur

**GET** `/api/users/profile`

Récupère les informations du profil de l'utilisateur authentifié.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Réponse (200)**
```json
{
  "id": 1,
  "email": "client@example.com",
  "firstName": "Jean",
  "lastName": "Dupont",
  "phone": "+33612345678",
  "address": {
    "line1": "123 Rue de la République",
    "line2": "Appartement 4B",
    "city": "Paris",
    "postalCode": "75001",
    "country": "France"
  },
  "roles": ["CLIENT"]
}
```

### Mettre à jour le profil utilisateur

**PUT** `/api/users/profile`

Met à jour les informations du profil de l'utilisateur authentifié.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Corps de la requête**
```json
{
  "firstName": "Jean",
  "lastName": "Durand",
  "phone": "+33687654321",
  "address": {
    "line1": "456 Avenue des Champs-Élysées",
    "city": "Paris",
    "postalCode": "75008",
    "country": "France"
  }
}
```

**Réponse (200)**
```json
{
  "id": 1,
  "email": "client@example.com",
  "firstName": "Jean",
  "lastName": "Durand",
  "phone": "+33687654321",
  "address": {
    "line1": "456 Avenue des Champs-Élysées",
    "city": "Paris",
    "postalCode": "75008",
    "country": "France"
  },
  "roles": ["CLIENT"]
}
```

### Changer le mot de passe

**PUT** `/api/users/password`

Change le mot de passe de l'utilisateur authentifié.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Corps de la requête**
```json
{
  "currentPassword": "Password123!",
  "newPassword": "NewPassword456!"
}
```

**Réponse (200)**
```json
{
  "message": "Mot de passe mis à jour avec succès"
}
```

### Lister les utilisateurs (Admin/Gestionnaire)

**GET** `/api/users`

Récupère la liste des utilisateurs (pour les administrateurs et gestionnaires).

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de requête**
- `page`: Numéro de page (défaut: 0)
- `size`: Taille de la page (défaut: 20)
- `sort`: Champ de tri (défaut: id)
- `direction`: Direction du tri (asc/desc, défaut: asc)
- `role`: Filtrer par rôle (CLIENT, LIVREUR, GESTIONNAIRE, ADMIN)
- `active`: Filtrer par statut actif (true/false)

**Réponse (200)**
```json
{
  "content": [
    {
      "id": 1,
      "email": "client@example.com",
      "firstName": "Jean",
      "lastName": "Durand",
      "roles": ["CLIENT"],
      "active": true,
      "createdAt": "2023-05-15T10:30:00Z"
    },
    {
      "id": 2,
      "email": "livreur@example.com",
      "firstName": "Pierre",
      "lastName": "Martin",
      "roles": ["LIVREUR"],
      "active": true,
      "createdAt": "2023-05-10T14:20:00Z"
    }
  ],
  "pageable": {
    "page": 0,
    "size": 20,
    "sort": "id,ASC"
  },
  "totalElements": 2,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Créer un utilisateur (Admin)

**POST** `/api/users`

Crée un nouvel utilisateur avec le rôle spécifié (réservé aux administrateurs).

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Corps de la requête**
```json
{
  "email": "nouveau-livreur@example.com",
  "password": "Password123!",
  "firstName": "Paul",
  "lastName": "Lefebvre",
  "phone": "+33611223344",
  "roles": ["LIVREUR"]
}
```

**Réponse (201)**
```json
{
  "id": 3,
  "email": "nouveau-livreur@example.com",
  "firstName": "Paul",
  "lastName": "Lefebvre",
  "phone": "+33611223344",
  "roles": ["LIVREUR"],
  "active": true,
  "createdAt": "2023-06-20T09:15:00Z"
}
```

### Activer/Désactiver un utilisateur (Admin)

**PUT** `/api/users/{userId}/status`

Active ou désactive un utilisateur (réservé aux administrateurs).

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de chemin**
- `userId`: ID de l'utilisateur

**Corps de la requête**
```json
{
  "active": false
}
```

**Réponse (200)**
```json
{
  "id": 3,
  "email": "nouveau-livreur@example.com",
  "firstName": "Paul",
  "lastName": "Lefebvre",
  "roles": ["LIVREUR"],
  "active": false,
  "updatedAt": "2023-06-25T11:30:00Z"
}
```

## Gestion des colis

### Créer une commande de colis

**POST** `/api/parcels/orders`

Crée une nouvelle commande de livraison de colis.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Corps de la requête**
```json
{
  "parcel": {
    "weight": 2.5,
    "length": 30,
    "width": 20,
    "height": 15,
    "contentDescription": "Vêtements",
    "declaredValue": 100.00
  },
  "recipient": {
    "name": "Marie Dubois",
    "phone": "+33655443322",
    "address": {
      "line1": "789 Boulevard Saint-Germain",
      "city": "Paris",
      "postalCode": "75006",
      "country": "France"
    }
  },
  "serviceType": "STANDARD",
  "insurance": true,
  "signatureRequired": false
}
```

**Réponse (201)**
```json
{
  "id": 1,
  "trackingNumber": "TRK202306250001",
  "parcel": {
    "id": 1,
    "weight": 2.5,
    "length": 30,
    "width": 20,
    "height": 15,
    "contentDescription": "Vêtements",
    "declaredValue": 100.00
  },
  "sender": {
    "id": 1,
    "name": "Jean Durand",
    "email": "client@example.com"
  },
  "recipient": {
    "name": "Marie Dubois",
    "phone": "+33655443322",
    "address": {
      "line1": "789 Boulevard Saint-Germain",
      "city": "Paris",
      "postalCode": "75006",
      "country": "France"
    }
  },
  "serviceType": "STANDARD",
  "status": "BORNE",
  "price": 15.50,
  "insurance": true,
  "signatureRequired": false,
  "createdAt": "2023-06-25T14:30:00Z"
}
```

### Obtenir les détails d'une commande

**GET** `/api/parcels/orders/{orderId}`

Récupère les détails d'une commande de colis.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de chemin**
- `orderId`: ID de la commande

**Réponse (200)**
```json
{
  "id": 1,
  "trackingNumber": "TRK202306250001",
  "parcel": {
    "id": 1,
    "weight": 2.5,
    "length": 30,
    "width": 20,
    "height": 15,
    "contentDescription": "Vêtements",
    "declaredValue": 100.00
  },
  "sender": {
    "id": 1,
    "name": "Jean Durand",
    "email": "client@example.com"
  },
  "recipient": {
    "name": "Marie Dubois",
    "phone": "+33655443322",
    "address": {
      "line1": "789 Boulevard Saint-Germain",
      "city": "Paris",
      "postalCode": "75006",
      "country": "France"
    }
  },
  "serviceType": "STANDARD",
  "status": "BORNE",
  "price": 15.50,
  "insurance": true,
  "signatureRequired": false,
  "createdAt": "2023-06-25T14:30:00Z",
  "updatedAt": "2023-06-25T14:30:00Z"
}
```

### Lister les commandes d'un client

**GET** `/api/parcels/orders`

Récupère la liste des commandes de l'utilisateur authentifié (client) ou de tous les clients (gestionnaire/admin).

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de requête**
- `page`: Numéro de page (défaut: 0)
- `size`: Taille de la page (défaut: 20)
- `sort`: Champ de tri (défaut: createdAt)
- `direction`: Direction du tri (asc/desc, défaut: desc)
- `status`: Filtrer par statut (BORNE, VALIDEE, PAYEE, etc.)
- `startDate`: Date de début (format: YYYY-MM-DD)
- `endDate`: Date de fin (format: YYYY-MM-DD)

**Réponse (200)**
```json
{
  "content": [
    {
      "id": 1,
      "trackingNumber": "TRK202306250001",
      "recipientName": "Marie Dubois",
      "serviceType": "STANDARD",
      "status": "BORNE",
      "price": 15.50,
      "createdAt": "2023-06-25T14:30:00Z"
    },
    {
      "id": 2,
      "trackingNumber": "TRK202206200005",
      "recipientName": "Pierre Martin",
      "serviceType": "EXPRESS",
      "status": "LIVREE",
      "price": 25.00,
      "createdAt": "2023-06-20T09:15:00Z"
    }
  ],
  "pageable": {
    "page": 0,
    "size": 20,
    "sort": "createdAt,DESC"
  },
  "totalElements": 2,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Mettre à jour le statut d'une commande (Livreur/Gestionnaire)

**PUT** `/api/parcels/orders/{orderId}/status`

Met à jour le statut d'une commande de colis.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de chemin**
- `orderId`: ID de la commande

**Corps de la requête**
```json
{
  "status": "EN_COURS",
  "location": "En route vers Paris 6ème",
  "notes": "Le colis est en cours de livraison"
}
```

**Réponse (200)**
```json
{
  "id": 1,
  "trackingNumber": "TRK202306250001",
  "status": "EN_COURS",
  "updatedAt": "2023-06-25T16:45:00Z"
}
```

### Suivre un colis par numéro de suivi

**GET** `/api/parcels/tracking/{trackingNumber}`

Récupère les informations de suivi d'un colis par son numéro de suivi (public).

**Paramètres de chemin**
- `trackingNumber`: Numéro de suivi du colis

**Réponse (200)**
```json
{
  "trackingNumber": "TRK202306250001",
  "status": "EN_COURS",
  "estimatedDelivery": "2023-06-26T18:00:00Z",
  "events": [
    {
      "status": "BORNE",
      "location": "Paris",
      "description": "Commande créée",
      "timestamp": "2023-06-25T14:30:00Z"
    },
    {
      "status": "VALIDEE",
      "location": "Paris",
      "description": "Commande validée et en préparation",
      "timestamp": "2023-06-25T15:15:00Z"
    },
    {
      "status": "ASSIGNEE",
      "location": "Paris",
      "description": "Colis assigné à un livreur",
      "timestamp": "2023-06-25T16:00:00Z"
    },
    {
      "status": "EN_COURS",
      "location": "En route vers Paris 6ème",
      "description": "Le colis est en cours de livraison",
      "timestamp": "2023-06-25T16:45:00Z"
    }
  ]
}
```

## Planification des tournées

### Créer une tournée de livraison

**POST** `/api/routes`

Crée une nouvelle tournée de livraison.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Corps de la requête**
```json
{
  "courierId": 2,
  "zoneId": 1,
  "routeDate": "2023-06-26",
  "startTime": "08:00",
  "notes": "Tournée spéciale pour les colis express"
}
```

**Réponse (201)**
```json
{
  "id": 1,
  "courier": {
    "id": 2,
    "name": "Pierre Martin",
    "phone": "+33699887766"
  },
  "zone": {
    "id": 1,
    "name": "Paris Centre",
    "postalCodes": ["75001", "75002", "75003", "75004"]
  },
  "routeDate": "2023-06-26",
  "status": "PLANNED",
  "startTime": "08:00",
  "notes": "Tournée spéciale pour les colis express",
  "createdAt": "2023-06-25T17:30:00Z"
}
```

### Obtenir les détails d'une tournée

**GET** `/api/routes/{routeId}`

Récupère les détails d'une tournée de livraison.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de chemin**
- `routeId`: ID de la tournée

**Réponse (200)**
```json
{
  "id": 1,
  "courier": {
    "id": 2,
    "name": "Pierre Martin",
    "phone": "+33699887766"
  },
  "zone": {
    "id": 1,
    "name": "Paris Centre",
    "postalCodes": ["75001", "75002", "75003", "75004"]
  },
  "routeDate": "2023-06-26",
  "status": "PLANNED",
  "startTime": "08:00",
  "notes": "Tournée spéciale pour les colis express",
  "tasks": [
    {
      "id": 1,
      "order": {
        "id": 1,
        "trackingNumber": "TRK202306250001",
        "recipientName": "Marie Dubois",
        "recipientAddress": "789 Boulevard Saint-Germain, Paris 75006",
        "serviceType": "STANDARD"
      },
      "taskOrder": 1,
      "status": "PENDING",
      "plannedArrivalTime": "09:30"
    },
    {
      "id": 2,
      "order": {
        "id": 2,
        "trackingNumber": "TRK202306240003",
        "recipientName": "Sophie Lefevre",
        "recipientAddress": "123 Rue de Rivoli, Paris 75001",
        "serviceType": "EXPRESS"
      },
      "taskOrder": 2,
      "status": "PENDING",
      "plannedArrivalTime": "11:00"
    }
  ],
  "createdAt": "2023-06-25T17:30:00Z",
  "updatedAt": "2023-06-25T17:30:00Z"
}
```

### Lister les tournées

**GET** `/api/routes`

Récupère la liste des tournées de livraison.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de requête**
- `page`: Numéro de page (défaut: 0)
- `size`: Taille de la page (défaut: 20)
- `sort`: Champ de tri (défaut: routeDate)
- `direction`: Direction du tri (asc/desc, défaut: asc)
- `courierId`: Filtrer par livreur
- `zoneId`: Filtrer par zone
- `status`: Filtrer par statut (PLANNED, IN_PROGRESS, COMPLETED, CANCELLED)
- `startDate`: Date de début (format: YYYY-MM-DD)
- `endDate`: Date de fin (format: YYYY-MM-DD)

**Réponse (200)**
```json
{
  "content": [
    {
      "id": 1,
      "courierName": "Pierre Martin",
      "zoneName": "Paris Centre",
      "routeDate": "2023-06-26",
      "status": "PLANNED",
      "taskCount": 5,
      "completedTaskCount": 0
    },
    {
      "id": 2,
      "courierName": "Paul Lefebvre",
      "zoneName": "Paris Ouest",
      "routeDate": "2023-06-26",
      "status": "IN_PROGRESS",
      "taskCount": 8,
      "completedTaskCount": 3
    }
  ],
  "pageable": {
    "page": 0,
    "size": 20,
    "sort": "routeDate,ASC"
  },
  "totalElements": 2,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Lister les tournées d'un livreur

**GET** `/api/routes/courier`

Récupère la liste des tournées du livreur authentifié.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de requête**
- `date`: Date spécifique (format: YYYY-MM-DD, défaut: aujourd'hui)

**Réponse (200)**
```json
{
  "content": [
    {
      "id": 1,
      "zoneName": "Paris Centre",
      "routeDate": "2023-06-26",
      "status": "PLANNED",
      "startTime": "08:00",
      "taskCount": 5,
      "completedTaskCount": 0
    }
  ],
  "pageable": {
    "page": 0,
    "size": 20,
    "sort": "routeDate,ASC"
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Assigner des colis à une tournée

**POST** `/api/routes/{routeId}/tasks`

Ajoute des tâches de livraison à une tournée existante.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de chemin**
- `routeId`: ID de la tournée

**Corps de la requête**
```json
{
  "orderIds": [3, 4, 5]
}
```

**Réponse (201)**
```json
{
  "message": "3 tâches ajoutées à la tournée",
  "tasks": [
    {
      "id": 3,
      "orderId": 3,
      "taskOrder": 3,
      "status": "PENDING"
    },
    {
      "id": 4,
      "orderId": 4,
      "taskOrder": 4,
      "status": "PENDING"
    },
    {
      "id": 5,
      "orderId": 5,
      "taskOrder": 5,
      "status": "PENDING"
    }
  ]
}
```

### Mettre à jour le statut d'une tâche de livraison

**PUT** `/api/routes/tasks/{taskId}`

Met à jour le statut d'une tâche de livraison.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de chemin**
- `taskId`: ID de la tâche

**Corps de la requête**
```json
{
  "status": "COMPLETED",
  "notes": "Livré avec signature",
  "actualArrivalTime": "09:25"
}
```

**Réponse (200)**
```json
{
  "id": 1,
  "orderId": 1,
  "status": "COMPLETED",
  "actualArrivalTime": "09:25",
  "notes": "Livré avec signature",
  "updatedAt": "2023-06-26T09:25:00Z"
}
```

### Signaler un incident de livraison

**POST** `/api/routes/tasks/{taskId}/incidents`

Signale un incident lors de la livraison d'un colis.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de chemin**
- `taskId`: ID de la tâche

**Corps de la requête**
```json
{
  "incidentType": "CUSTOMER_ABSENT",
  "description": "Client absent, impossible de contacter par téléphone",
  "requiresReschedule": true
}
```

**Réponse (201)**
```json
{
  "id": 1,
  "taskId": 1,
  "incidentType": "CUSTOMER_ABSENT",
  "description": "Client absent, impossible de contacter par téléphone",
  "resolved": false,
  "createdAt": "2023-06-26T09:30:00Z"
}
```

## Suivi des colis

### Obtenir l'historique des statuts d'un colis

**GET** `/api/parcels/{parcelId}/status`

Récupère l'historique complet des changements de statut d'un colis.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de chemin**
- `parcelId`: ID du colis

**Réponse (200)**
```json
{
  "parcelId": 1,
  "trackingNumber": "TRK202306250001",
  "statusHistory": [
    {
      "id": 1,
      "status": "BORNE",
      "location": "Paris",
      "description": "Commande créée",
      "timestamp": "2023-06-25T14:30:00Z"
    },
    {
      "id": 2,
      "status": "VALIDEE",
      "location": "Paris",
      "description": "Commande validée et en préparation",
      "timestamp": "2023-06-25T15:15:00Z"
    },
    {
      "id": 3,
      "status": "ASSIGNEE",
      "location": "Paris",
      "description": "Colis assigné à un livreur",
      "timestamp": "2023-06-25T16:00:00Z"
    },
    {
      "id": 4,
      "status": "EN_COURS",
      "location": "En route vers Paris 6ème",
      "description": "Le colis est en cours de livraison",
      "timestamp": "2023-06-25T16:45:00Z"
    }
  ]
}
```

## Notifications

### Obtenir les notifications d'un utilisateur

**GET** `/api/notifications`

Récupère la liste des notifications de l'utilisateur authentifié.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de requête**
- `page`: Numéro de page (défaut: 0)
- `size`: Taille de la page (défaut: 20)
- `sort`: Champ de tri (défaut: createdAt)
- `direction`: Direction du tri (asc/desc, défaut: desc)
- `type`: Filtrer par type (EMAIL, SMS, PUSH)
- `status`: Filtrer par statut (PENDING, SENT, FAILED)
- `read`: Filtrer par statut de lecture (true/false)

**Réponse (200)**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Votre colis est en cours de livraison",
      "message": "Le colis TRK202306250001 est actuellement en cours de livraison.",
      "type": "EMAIL",
      "status": "SENT",
      "read": false,
      "createdAt": "2023-06-25T16:45:00Z",
      "sentAt": "2023-06-25T16:46:00Z"
    },
    {
      "id": 2,
      "title": "Votre colis a été livré",
      "message": "Le colis TRK202206200005 a été livré avec succès.",
      "type": "EMAIL",
      "status": "SENT",
      "read": true,
      "createdAt": "2023-06-20T18:30:00Z",
      "sentAt": "2023-06-20T18:31:00Z"
    }
  ],
  "pageable": {
    "page": 0,
    "size": 20,
    "sort": "createdAt,DESC"
  },
  "totalElements": 2,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Marquer une notification comme lue

**PUT** `/api/notifications/{notificationId}/read`

Marque une notification comme lue.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de chemin**
- `notificationId`: ID de la notification

**Réponse (200)**
```json
{
  "id": 1,
  "read": true,
  "readAt": "2023-06-25T20:15:00Z"
}
```

### Obtenir les préférences de notification

**GET** `/api/notifications/preferences`

Récupère les préférences de notification de l'utilisateur authentifié.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Réponse (200)**
```json
{
  "emailEnabled": true,
  "smsEnabled": false,
  "pushEnabled": true,
  "events": {
    "COLIS_CREE": {
      "email": true,
      "sms": false,
      "push": true
    },
    "COLIS_ASSIGNE": {
      "email": true,
      "sms": false,
      "push": true
    },
    "COLIS_EN_LIVRAISON": {
      "email": true,
      "sms": true,
      "push": true
    },
    "COLIS_LIVRE": {
      "email": true,
      "sms": false,
      "push": true
    },
    "COLIS_INCIDENT": {
      "email": true,
      "sms": true,
      "push": true
    }
  }
}
```

### Mettre à jour les préférences de notification

**PUT** `/api/notifications/preferences`

Met à jour les préférences de notification de l'utilisateur authentifié.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Corps de la requête**
```json
{
  "emailEnabled": true,
  "smsEnabled": true,
  "pushEnabled": true,
  "events": {
    "COLIS_CREE": {
      "email": true,
      "sms": false,
      "push": true
    },
    "COLIS_ASSIGNE": {
      "email": true,
      "sms": false,
      "push": true
    },
    "COLIS_EN_LIVRAISON": {
      "email": true,
      "sms": true,
      "push": true
    },
    "COLIS_LIVRE": {
      "email": true,
      "sms": true,
      "push": true
    },
    "COLIS_INCIDENT": {
      "email": true,
      "sms": true,
      "push": true
    }
  }
}
```

**Réponse (200)**
```json
{
  "message": "Préférences de notification mises à jour avec succès"
}
```

## Rapports

### Obtenir les statistiques de livraison

**GET** `/api/reports/delivery-stats`

Récupère les statistiques de livraison pour une période donnée.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de requête**
- `startDate`: Date de début (format: YYYY-MM-DD)
- `endDate`: Date de fin (format: YYYY-MM-DD)
- `zoneId`: Filtrer par zone (optionnel)
- `courierId`: Filtrer par livreur (optionnel)

**Réponse (200)**
```json
{
  "period": {
    "startDate": "2023-06-01",
    "endDate": "2023-06-30"
  },
  "summary": {
    "totalParcels": 1250,
    "deliveredParcels": 1180,
    "failedDeliveries": 45,
    "pendingDeliveries": 25,
    "successRate": 94.4
  },
  "byStatus": [
    {
      "status": "LIVREE",
      "count": 1180,
      "percentage": 94.4
    },
    {
      "status": "ECHEC_LIVRAISON",
      "count": 45,
      "percentage": 3.6
    },
    {
      "status": "EN_COURS",
      "count": 25,
      "percentage": 2.0
    }
  ],
  "byZone": [
    {
      "zoneName": "Paris Centre",
      "totalParcels": 450,
      "deliveredParcels": 425,
      "successRate": 94.4
    },
    {
      "zoneName": "Paris Ouest",
      "totalParcels": 380,
      "deliveredParcels": 360,
      "successRate": 94.7
    },
    {
      "zoneName": "Paris Est",
      "totalParcels": 420,
      "deliveredParcels": 395,
      "successRate": 94.0
    }
  ],
  "byCourier": [
    {
      "courierName": "Pierre Martin",
      "totalParcels": 310,
      "deliveredParcels": 295,
      "successRate": 95.2
    },
    {
      "courierName": "Paul Lefebvre",
      "totalParcels": 290,
      "deliveredParcels": 270,
      "successRate": 93.1
    },
    {
      "courierName": "Marie Dubois",
      "totalParcels": 280,
      "deliveredParcels": 265,
      "successRate": 94.6
    }
  ],
  "byServiceType": [
    {
      "serviceType": "STANDARD",
      "count": 950,
      "percentage": 76.0
    },
    {
      "serviceType": "EXPRESS",
      "count": 300,
      "percentage": 24.0
    }
  ]
}
```

### Obtenir les rapports d'incidents

**GET** `/api/reports/incidents`

Récupère les rapports d'incidents pour une période donnée.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de requête**
- `page`: Numéro de page (défaut: 0)
- `size`: Taille de la page (défaut: 20)
- `sort`: Champ de tri (défaut: createdAt)
- `direction`: Direction du tri (asc/desc, défaut: desc)
- `startDate`: Date de début (format: YYYY-MM-DD)
- `endDate`: Date de fin (format: YYYY-MM-DD)
- `incidentType`: Filtrer par type d'incident
- `zoneId`: Filtrer par zone
- `courierId`: Filtrer par livreur
- `resolved`: Filtrer par statut de résolution (true/false)

**Réponse (200)**
```json
{
  "content": [
    {
      "id": 1,
      "taskId": 15,
      "trackingNumber": "TRK202306200015",
      "incidentType": "CUSTOMER_ABSENT",
      "description": "Client absent, impossible de contacter par téléphone",
      "resolved": true,
      "resolutionNotes": "Nouvelle tentative de livraison programmée pour le lendemain",
      "courierName": "Pierre Martin",
      "zoneName": "Paris Centre",
      "createdAt": "2023-06-20T14:30:00Z",
      "resolvedAt": "2023-06-21T09:15:00Z"
    },
    {
      "id": 2,
      "taskId": 23,
      "trackingNumber": "TRK202306210008",
      "incidentType": "ADDRESS_NOT_FOUND",
      "description": "Adresse introuvable, le numéro n'existe pas dans cette rue",
      "resolved": false,
      "courierName": "Paul Lefebvre",
      "zoneName": "Paris Ouest",
      "createdAt": "2023-06-21T16:45:00Z"
    }
  ],
  "pageable": {
    "page": 0,
    "size": 20,
    "sort": "createdAt,DESC"
  },
  "totalElements": 2,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Obtenir les statistiques des incidents

**GET** `/api/reports/incident-stats`

Récupère les statistiques des incidents pour une période donnée.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de requête**
- `startDate`: Date de début (format: YYYY-MM-DD)
- `endDate`: Date de fin (format: YYYY-MM-DD)
- `zoneId`: Filtrer par zone (optionnel)
- `courierId`: Filtrer par livreur (optionnel)

**Réponse (200)**
```json
{
  "period": {
    "startDate": "2023-06-01",
    "endDate": "2023-06-30"
  },
  "summary": {
    "totalIncidents": 45,
    "resolvedIncidents": 40,
    "pendingIncidents": 5,
    "resolutionRate": 88.9
  },
  "byType": [
    {
      "incidentType": "CUSTOMER_ABSENT",
      "count": 20,
      "percentage": 44.4
    },
    {
      "incidentType": "ADDRESS_NOT_FOUND",
      "count": 10,
      "percentage": 22.2
    },
    {
      "incidentType": "DAMAGED_PARCEL",
      "count": 8,
      "percentage": 17.8
    },
    {
      "incidentType": "REFUSED_DELIVERY",
      "count": 7,
      "percentage": 15.6
    }
  ],
  "byZone": [
    {
      "zoneName": "Paris Centre",
      "totalIncidents": 18,
      "resolvedIncidents": 16,
      "resolutionRate": 88.9
    },
    {
      "zoneName": "Paris Ouest",
      "totalIncidents": 12,
      "resolvedIncidents": 11,
      "resolutionRate": 91.7
    },
    {
      "zoneName": "Paris Est",
      "totalIncidents": 15,
      "resolvedIncidents": 13,
      "resolutionRate": 86.7
    }
  ],
  "byCourier": [
    {
      "courierName": "Pierre Martin",
      "totalIncidents": 12,
      "resolvedIncidents": 11,
      "resolutionRate": 91.7
    },
    {
      "courierName": "Paul Lefebvre",
      "totalIncidents": 18,
      "resolvedIncidents": 15,
      "resolutionRate": 83.3
    },
    {
      "courierName": "Marie Dubois",
      "totalIncidents": 15,
      "resolvedIncidents": 14,
      "resolutionRate": 93.3
    }
  ]
}
```

### Obtenir les performances des livreurs

**GET** `/api/reports/courier-performance`

Récupère les rapports de performance des livreurs pour une période donnée.

**En-têtes**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Paramètres de requête**
- `startDate`: Date de début (format: YYYY-MM-DD)
- `endDate`: Date de fin (format: YYYY-MM-DD)
- `zoneId`: Filtrer par zone (optionnel)

**Réponse (200)**
```json
{
  "period": {
    "startDate": "2023-06-01",
    "endDate": "2023-06-30"
  },
  "couriers": [
    {
      "courierId": 2,
      "courierName": "Pierre Martin",
      "totalRoutes": 22,
      "totalParcels": 310,
      "deliveredParcels": 295,
      "failedDeliveries": 10,
      "pendingDeliveries": 5,
      "successRate": 95.2,
      "averageDeliveryTime": "14:30",
      "totalDistance": 845.5,
      "averageRouteTime": "7h 25m",
      "incidents": 12,
      "resolvedIncidents": 11,
      "incidentResolutionRate": 91.7
    },
    {
      "courierId": 3,
      "courierName": "Paul Lefebvre",
      "totalRoutes": 20,
      "totalParcels": 290,
      "deliveredParcels": 270,
      "failedDeliveries": 15,
      "pendingDeliveries": 5,
      "successRate": 93.1,
      "averageDeliveryTime": "15:15",
      "totalDistance": 792.3,
      "averageRouteTime": "7h 45m",
      "incidents": 18,
      "resolvedIncidents": 15,
      "incidentResolutionRate": 83.3
    },
    {
      "courierId": 4,
      "courierName": "Marie Dubois",
      "totalRoutes": 21,
      "totalParcels": 280,
      "deliveredParcels": 265,
      "failedDeliveries": 10,
      "pendingDeliveries": 5,
      "successRate": 94.6,
      "averageDeliveryTime": "14:45",
      "totalDistance": 812.7,
      "averageRouteTime": "7h 30m",
      "incidents": 15,
      "resolvedIncidents": 14,
      "incidentResolutionRate": 93.3
    }
  ]
}
```

## Erreurs

### Codes d'erreur communs

| Code HTTP | Code d'erreur | Description |
|-----------|---------------|-------------|
| 400 | BAD_REQUEST | Requête mal formée |
| 401 | UNAUTHORIZED | Non authentifié |
| 403 | FORBIDDEN | Accès refusé (permissions insuffisantes) |
| 404 | NOT_FOUND | Ressource non trouvée |
| 409 | CONFLICT | Conflit de données (ex: email déjà utilisé) |
| 422 | UNPROCESSABLE_ENTITY | Données invalides (validation échouée) |
| 500 | INTERNAL_SERVER_ERROR | Erreur interne du serveur |

### Format des réponses d'erreur

```json
{
  "timestamp": "2023-06-25T14:30:00Z",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Commande non trouvée",
  "path": "/api/parcels/orders/999"
}
```

### Erreurs de validation

```json
{
  "timestamp": "2023-06-25T14:30:00Z",
  "status": 422,
  "error": "UNPROCESSABLE_ENTITY",
  "message": "Données invalides",
  "errors": [
    {
      "field": "parcel.weight",
      "message": "Le poids doit être supérieur à 0"
    },
    {
      "field": "recipient.name",
      "message": "Le nom du destinataire est obligatoire"
    }
  ]
}
```
