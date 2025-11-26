# DistributionColisRealTime

## Vue d'ensemble

DistributionColisRealTime est une application web pour la gestion complète du cycle de vie des colis, permettant aux clients de créer des envois, aux livreurs de gérer leurs tournées, et aux gestionnaires de superviser l'ensemble du processus de distribution.

## Architecture

L'application est construite avec une architecture microservices :

- **Backend** : Spring Boot (Java 23) avec Spring Security pour l'authentification et Apache Kafka pour la communication asynchrone
- **Frontend** : Vue.js 3 avec Composition API, Pinia pour la gestion d'état et Tailwind CSS pour le style
- **Base de données** : PostgreSQL pour la persistance des données
- **Infrastructure** : Docker et docker-compose pour le déploiement

## Fonctionnalités principales

- Gestion des comptes utilisateurs (Client, Livreur, Gestionnaire, Administrateur)
- Création et suivi des commandes de colis
- Planification et optimisation des tournées de livraison
- Notifications en temps réel via Kafka
- Tableaux de bord et rapports pour les gestionnaires

## Démarrage rapide

### Prérequis

- Docker et Docker Compose
- Java 23 (pour le développement local)
- Node.js 18+ (pour le développement local du frontend)

### Lancement avec Docker

```bash
# Cloner le dépôt
git clone <repository-url>
cd DistributionColisRealTime

# Lancer tous les services
docker-compose up -d
```

L'application sera accessible à l'adresse http://localhost

### Développement local

#### Backend

```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Documentation

- [Spécifications techniques](docs/specifications.md)
- [Guide de développement](docs/development.md)
- [API Documentation](docs/api.md)
