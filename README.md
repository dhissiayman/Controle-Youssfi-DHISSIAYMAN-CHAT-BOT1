# Chatbot Intelligent avec Spring AI & Telegram

## Description
Ce projet est une application de **Chatbot Intelligent** réalisée dans le cadre d'un **contrôle scolaire**. Il intègre plusieurs technologies avancées comme **Spring AI**, le protocole **MCP (Model Context Protocol)** et l'API **Telegram** pour fournir une interface conversationnelle capable de répondre aux questions en utilisant une base de connaissances locale (RAG) et des outils externes.

L'application agit comme un agent conversationnel qui peut :
- Discuter naturellement avec les utilisateurs via Telegram.
- Utiliser le **RAG (Retrieval-Augmented Generation)** pour répondre à partir de documents locaux.
- Exécuter des actions via des outils simulés ou connectés via MCP (ex: donner l'heure, effectuer une recherche).
- Garder en mémoire le contexte de la conversation.

## Objectifs du projet (pour le contrôle)
Ce projet vise à démontrer la maîtrise des concepts suivants :
- **Développement Back-end Java** avec le framework **Spring Boot**.
- **Intégration d'IA Générative** dans une application Java via **Spring AI**.
- Mise en œuvre du pattern **RAG (Retrieval-Augmented Generation)** avec une base vectorielle simple.
- Création d'un **Bot Telegram** interactif.
- Compréhension de l'architecture **Micro-services / Agents** avec l'ébauche d'un client MCP.
- Gestion de la **mémoire conversationnelle** et injection de contexte.

## Fonctionnalités principales
- **Interface Telegram** : Interaction fluide avec l'utilisateur via l'application Telegram.
- **RAG Local (Retrieval-Augmented Generation)** :
  - Ingestion automatique de fichiers texte depuis `src/main/resources/rag-data`.
  - Découpage et vectorisation des documents.
  - Recherche sémantique pour enrichir les réponses de l'IA.
- **Support des modèles locaux** : Configuration pour utiliser Llama 3.2 via **Ollama**.
- **Intégration MCP (Model Context Protocol)** : Capacité du bot à appeler des outils externes (ex: outil "Time", outil "Search").
- **Mémoire de conversation** : Le bot se souvient des derniers échanges pour maintenir le contexte.

## Stack technique
- **Langage** : Java 17
- **Framework Principal** : Spring Boot 3.2.3
- **IA & LLM** :
  - **Spring AI 0.8.1** (Orchestration IA)
  - **Ollama** (Exécution locale des modèles Llama 3.2 et Nomic Embed)
  - **SimpleVectorStore** (Base de données vectorielle en mémoire/fichier)
- **Messagerie** : Telegram Bots 6.9.7.1
- **Build Tool** : Maven
- **Outils** : Lombok (pour réduire le boilerplate code)

## Structure du projet
Voici une vue simplifiée des dossiers et fichiers importants :

```
chatbot-spring-ai-mcp-telegram-client/
├── src/main/java/com/example/mydhissia/
│   ├── ai/                 # Gestion du Chat, Mémoire et Prompt Engineering
│   │   ├── ChatService.java
│   │   └── ConversationMemoryService.java
│   ├── config/             # Configuration Spring (Telegram, AI)
│   ├── mcp/                # Client et Service pour le Model Context Protocol
│   ├── rag/                # Logique RAG (Ingestion et Recherche Vectorielle)
│   │   ├── RagIngestionService.java
│   │   └── RagQueryService.java
│   ├── telegram/           # Gestion du Bot Telegram
│   │   ├── MyDHISSIAiAgentBot.java
│   │   └── TelegramUpdateHandler.java
│   └── MyDhissiaAiAgentApplication.java  # Point d'entrée
├── src/main/resources/
│   ├── rag-data/           # Dossier contenant les documents pour le RAG
│   ├── application.yaml    # Fichier de configuration principal
│   └── vectorstore.json    # Fichier de persistance des vecteurs (généré)
├── pom.xml                 # Dépendances Maven
└── README.md               # Documentation du projet
```

## Prérequis
Avant de lancer le projet, assurez-vous d'avoir installé :

1.  **Java JDK 17** (ou supérieur).
2.  **Maven** (pour construire le projet).
3.  **Ollama** (pour faire tourner le modèle localement).
    - Modèles requis :
      - `ollama pull llama3.2:1b` (pour le chat)
      - `ollama pull nomic-embed-text` (pour les embeddings)

## Installation

1.  **Cloner le dépôt** :
    ```bash
    git clone <URL_DU_DEPOT>
    cd chatbot-spring-ai-mcp-telegram-client
    ```

2.  **Installer les dépendances** :
    ```bash
    mvn clean install
    ```

## Configuration

La configuration se trouve dans `src/main/resources/application.yaml`.

**Variables importantes :**
- `spring.ai.ollama.base-url` : URL du serveur Ollama (par défaut `http://localhost:11434`).
- `telegram.bot.token` : Le token d'accès de votre bot Telegram (à obtenir via BotFather).
- `telegram.bot.username` : Le nom d'utilisateur de votre bot.

*Note : Pour le contrôle, le token est présent dans le fichier, mais dans un projet réel, il devrait être passé en variable d'environnement.*

## Lancement du projet

1.  **Démarrer Ollama** (dans un terminal séparé) :
    Assurez-vous qu'Ollama tourne en arrière-plan.

2.  **Lancer l'application Spring Boot** :
    Via Maven :
    ```bash
    mvn spring-boot:run
    ```
    Ou en exécutant la classe principale `MyDhissiaAiAgentApplication` depuis votre IDE (IntelliJ, Eclipse, VS Code).

Une fois lancé, vous verrez des logs indiquant que le bot a démarré et que l'ingestion RAG (si nécessaire) est terminée.

## Utilisation

1.  Ouvrez l'application **Telegram**.
2.  Cherchez votre bot : **@MyDHISSIAiAgentBot**.
3.  Cliquez sur **Démarrer** (/start).
4.  Posez une question.
    - Le bot répondra en utilisant ses connaissances générales ou le contenu des fichiers dans `rag-data`.
    - Essayez d'utiliser le mot-clé "time" pour voir l'appel d'outil simulé (ex: "What time is it?").

## Tests
Le projet inclut des tests unitaires standards générés par Spring Boot.
Pour les lancer :
```bash
mvn test
```

## Problèmes connus / Limitations
- **Détection des outils simplifiée** : L'appel des outils (Mcp Tools) est actuellement basé sur une détection de mots-clés (regex) simple dans `ChatService` au lieu d'utiliser le "Function Calling" natif du modèle, pour simplifier la démo.
- **Sécurité** : Le token Telegram est en clair dans `application.yaml`.
- **Persistance** : Le `SimpleVectorStore` stocke les vecteurs dans un fichier JSON local (`vectorstore.json`), ce qui n'est pas adapté pour de très gros volumes de données.

## Améliorations possibles
- Implémenter le **Function Calling** natif supporté par Spring AI pour une détection plus robuste des outils.
- Utiliser une vraie base de données vectorielle comme **ChromaDB** ou **PGVector**.
- Sécuriser les clés API via des variables d'environnement.
- Ajouter une interface Web pour uploader dynamiquement des documents dans le RAG.

## Auteur
**DHISSI AYMAN**
*Projet réalisé pour un contrôle scolaire.*