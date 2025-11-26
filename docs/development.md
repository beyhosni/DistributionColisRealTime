# Guide de Développement - DistributionColisRealTime

## Table des matières

1. [Configuration de l'environnement](#configuration-de-lenvironnement)
2. [Structure du projet](#structure-du-projet)
3. [Conventions de code](#conventions-de-code)
4. [Processus de développement](#processus-de-développement)
5. [Tests](#tests)
6. [Déploiement](#déploiement)

## Configuration de l'environnement

### Prérequis

- Java 23
- Maven 3.8+
- Node.js 18+
- Docker et Docker Compose
- PostgreSQL 15+ (si développement local sans Docker)
- Git

### Configuration du backend

1. Clonez le dépôt :
```bash
git clone <repository-url>
cd DistributionColisRealTime
```

2. Configuration de la base de données :
   - Option 1: Utiliser Docker Compose (recommandé) :
   ```bash
   docker-compose up -d postgres
   ```
   - Option 2: Installation locale de PostgreSQL

3. Configuration de Kafka :
   - Option 1: Utiliser Docker Compose (recommandé) :
   ```bash
   docker-compose up -d zookeeper kafka
   ```
   - Option 2: Installation locale de Kafka

4. Lancement du backend :
```bash
cd backend
./mvnw spring-boot:run
```

5. Vérifiez que le backend est accessible à http://localhost:8080

### Configuration du frontend

1. Installation des dépendances :
```bash
cd frontend
npm install
```

2. Lancement du serveur de développement :
```bash
npm run dev
```

3. Vérifiez que le frontend est accessible à http://localhost:5173

### Configuration des IDE

#### IntelliJ IDEA (Backend)

1. Installez les plugins suivants :
   - Lombok
   - Spring Boot
   - Docker
   - Database Navigator

2. Configurez le code style selon les conventions du projet (voir section Conventions de code)

#### VSCode (Frontend)

1. Installez les extensions suivantes :
   - Vue Language Features (Volar)
   - TypeScript Vue Plugin (Volar)
   - ESLint
   - Prettier
   - Tailwind CSS IntelliSense

2. Configurez les paramètres selon les conventions du projet (voir section Conventions de code)

## Structure du projet

### Structure du backend

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── distribution/
│   │   │           ├── colis/           # Package principal
│   │   │           │   ├── config/      # Configuration (Security, Kafka, etc.)
│   │   │           │   ├── controller/  # Contrôleurs REST
│   │   │           │   ├── dto/         # Data Transfer Objects
│   │   │           │   ├── entity/      # Entités JPA
│   │   │           │   ├── enums/       # Énumérations
│   │   │           │   ├── exception/   # Gestion des exceptions
│   │   │           │   ├── kafka/       # Producteurs/Consommateurs Kafka
│   │   │           │   ├── repository/  # Interfaces JPA Repository
│   │   │           │   ├── security/    # Configuration Spring Security
│   │   │           │   ├── service/     # Services métier
│   │   │           │   └── util/        # Classes utilitaires
│   │   │           └── ColisApplication.java
│   │   └── resources/
│   │       ├── application.yml          # Configuration principale
│   │       └── db/                     # Scripts de migration (si nécessaire)
│   └── test/                           # Tests
├── Dockerfile
└── pom.xml
```

### Structure du frontend

```
frontend/
├── public/
│   └── favicon.ico
├── src/
│   ├── assets/              # Images, icônes, etc.
│   ├── components/          # Composants réutilisables
│   │   ├── common/          # Composants communs
│   │   ├── forms/           # Composants de formulaire
│   │   └── ui/              # Composants d'interface
│   ├── composables/         # Composables Vue 3
│   ├── layouts/             # Mises en page
│   ├── pages/               # Pages de l'application
│   │   ├── auth/            # Pages d'authentification
│   │   ├── client/          # Pages client
│   │   ├── courier/         # Pages livreur
│   │   └── admin/           # Pages admin/gestionnaire
│   ├── plugins/             # Plugins Vue
│   ├── router/              # Configuration du routeur
│   ├── services/            # Services d'API
│   ├── stores/              # Stores Pinia
│   ├── types/               # Types TypeScript
│   ├── utils/               # Fonctions utilitaires
│   ├── App.vue              # Composant racine
│   └── main.ts              # Point d'entrée
├── Dockerfile
├── index.html
├── package.json
├── tailwind.config.js
├── tsconfig.json
└── vite.config.ts
```

## Conventions de code

### Backend (Java)

1. **Naming conventions** :
   - Classes : PascalCase (Ex: `ShipmentOrderService`)
   - Méthodes : camelCase (Ex: `createShipmentOrder`)
   - Constantes : UPPER_SNAKE_CASE (Ex: `MAX_PARCEL_WEIGHT`)
   - Variables : camelCase (Ex: `shipmentOrder`)

2. **Structure des classes** :
   ```java
   // Imports
   import org.springframework.stereotype.Service;

   // Annotations de classe
   @Service
   public class ShipmentOrderService {

       // Champs (constants first, then instance variables)
       private static final Logger logger = LoggerFactory.getLogger(ShipmentOrderService.class);
       private final ShipmentOrderRepository shipmentOrderRepository;

       // Constructeur
       public ShipmentOrderService(ShipmentOrderRepository shipmentOrderRepository) {
           this.shipmentOrderRepository = shipmentOrderRepository;
       }

       // Méthodes publiques
       public ShipmentOrder createShipmentOrder(CreateShipmentOrderRequest request) {
           // Implémentation
       }

       // Méthodes privées
       private BigDecimal calculatePrice(ShipmentOrder order) {
           // Implémentation
       }
   }
   ```

3. **Commentaires** :
   - Utiliser Javadoc pour les classes et méthodes publiques
   - Commenter uniquement la logique complexe ou non évidente

4. **Exceptions** :
   - Créer des exceptions spécifiques pour chaque cas d'erreur métier
   - Utiliser `@ControllerAdvice` pour la gestion centralisée des exceptions

### Frontend (TypeScript/Vue)

1. **Naming conventions** :
   - Composants : PascalCase (Ex: `ShipmentOrderForm.vue`)
   - Méthodes : camelCase (Ex: `createShipmentOrder`)
   - Constantes : UPPER_SNAKE_CASE (Ex: `API_BASE_URL`)
   - Variables : camelCase (Ex: `shipmentOrder`)

2. **Structure des composants** :
   ```vue
   <script setup lang="ts">
   // Imports
   import { ref, computed, onMounted } from 'vue';
   import { useShipmentOrderStore } from '@/stores/shipmentOrder';

   // Props et emits
   interface Props {
     orderId: string;
   }

   const props = defineProps<Props>();
   const emit = defineEmits(['order-created']);

   // Stores et services
   const shipmentOrderStore = useShipmentOrderStore();

   // Références réactives
   const isLoading = ref(false);
   const orderData = ref(null);

   // Computed
   const orderPrice = computed(() => {
     // Calcul
   });

   // Méthodes
   const createOrder = async () => {
     // Implémentation
   };

   // Lifecycle hooks
   onMounted(async () => {
     // Initialisation
   });
   </script>

   <template>
     <!-- Template HTML -->
   </template>

   <style scoped>
   /* Styles spécifiques au composant */
   </style>
   ```

3. **TypeScript** :
   - Toujours définir les types pour les props, données retournées par les API, etc.
   - Utiliser les interfaces pour les objets complexes

4. **Composition API** :
   - Privilégier la Composition API avec `<script setup>`
   - Utiliser les composables pour la logique réutilisable

## Processus de développement

### Git Workflow

Nous utilisons une variante de Git Flow avec les branches suivantes :

- `main` : Branche de production, toujours stable
- `develop` : Branche d'intégration, contient les dernières fonctionnalités
- `feature/*` : Branches de développement pour les nouvelles fonctionnalités
- `bugfix/*` : Branches pour corriger les bugs
- `release/*` : Branches pour préparer une nouvelle version

### Processus pour une nouvelle fonctionnalité

1. Créer une branche à partir de `develop` :
```bash
git checkout develop
git pull origin develop
git checkout -b feature/nouvelle-fonctionnalite
```

2. Développer la fonctionnalité en suivant les conventions de code

3. Committer régulièrement avec des messages clairs :
```bash
git commit -m "feat: ajouter la création de commande de colis"
```

4. Pousser la branche et créer une Pull Request vers `develop`

5. Code review par au moins un autre développeur

6. Corriger les éventuels retours

7. Fusionner la PR dans `develop`

### Processus pour une correction de bug

1. Créer une branche à partir de `develop` (ou `main` si bug critique en prod) :
```bash
git checkout develop
git pull origin develop
git checkout -b bugfix/correction-bug-calcul-prix
```

2. Corriger le bug en ajoutant si nécessaire un test pour éviter les régressions

3. Committer avec un message clair :
```bash
git commit -m "fix: correction du calcul du prix pour les colis lourds"
```

4. Pousser la branche et créer une Pull Request

5. Code review et fusion

## Tests

### Tests backend

1. **Tests unitaires** :
   - Utiliser JUnit 5
   - Mock des dépendances avec Mockito
   - Couvrir les services et les utilitaires

2. **Tests d'intégration** :
   - Utiliser Spring Boot Test
   - Utiliser Testcontainers pour les tests avec base de données réelle
   - Couvrir les contrôleurs et les repositories

3. **Tests de contrats** :
   - Utiliser Pact ou Spring Cloud Contract pour tester les contrats API

### Tests frontend

1. **Tests unitaires** :
   - Utiliser Vitest ou Jest
   - Tester les composants et les composables
   - Mock des appels API

2. **Tests d'intégration E2E** :
   - Utiliser Cypress ou Playwright
   - Couvrir les parcours utilisateur critiques

### Couverture de code

- Objectif de couverture minimale : 80%
- Prioriser les tests sur la logique métier critique
- Intégrer les tests dans la CI/CD

## Déploiement

### Processus de déploiement

1. **Développement** :
   - Les branches `feature/*` sont déployées dans des environnements de test automatiquement
   - Les PRs déclenchent des tests automatiques

2. **Staging** :
   - La branche `develop` est déployée dans l'environnement de staging après fusion
   - Tests manuels et validation par le Product Owner

3. **Production** :
   - Créer une branche `release/*` à partir de `develop`
   - Déployer en production après validation finale
   - Fusionner `release/*` dans `main` et `develop`

### Docker

Le projet utilise Docker pour la conteneurisation :

- **Backend** : Image basée sur Eclipse Temurin 21
- **Frontend** : Build en plusieurs étapes (Node.js pour le build, Nginx pour le runtime)
- **Services externes** : PostgreSQL, Kafka, Zookeeper

### CI/CD

La CI/CD est implémentée avec GitHub Actions ou GitLab CI/CD :

1. **Build** :
   - Compilation du backend
   - Build du frontend
   - Création des images Docker

2. **Tests** :
   - Exécution des tests unitaires et d'intégration
   - Analyse de qualité de code (SonarQube)
   - Scan de sécurité

3. **Déploiement** :
   - Déploiement automatique dans les environnements de test/staging
   - Déploiement en production après validation manuelle
