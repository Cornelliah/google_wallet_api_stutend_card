# Google Wallet Student Card 📘
<img width="655" height="399" alt="image" src="https://github.com/user-attachments/assets/eee84888-33a9-49ab-9279-5410b317c93f" />


## Description

Ce projet permet de générer une « carte étudiante » (student card) via l’API Google Wallet API.  
L’idée est de produire, depuis un backend Java, un objet carte (pass) compatible Google Wallet et utilisable comme carte étudiante numérique pour les utilisateurs.

## Fonctionnalités / Ce que ça fait

- Création d’une « classe de carte » (card class) adaptée aux cartes étudiantes.  
- Génération d’un objet carte (pass) attribué à un utilisateur spécifique.  
- Production d’un jeton JSON Web Token (JWT) signé, pour autoriser l’ajout de la carte dans Google Wallet.  
- Exemple de backend Java (Maven) pour gérer la création de carte.  

## Structure du projet

/

├── .mvn/ ← configuration Maven

├── src/ ← code source Java

├── pom.xml ← configuration du projet Maven

└── README.md ← ce fichier

## Architecture générale
          +-----------------------+
          | Google Wallet Issuer |
          | (Google Cloud)       |
          +----------+-----------+
                     |
             Service Account Key
                     |
                     v
    +------------------------------------+
    |   Backend Java (ce projet)         |
    |------------------------------------|
    | - Génère StudentCardClass          |
    | - Génère StudentCardObject         |
    | - Signe un JWT Wallet              |
    +------------------+-----------------+
                       |
                       | JWT
                       v
          +----------------------------+
          |  Client (Web / Mobile)     |
          |  Add to Google Wallet      |
          +----------------------------+

          
## 📝 Prérequis

- Java (version compatible avec Maven).  
- Maven (pour compiler et exécuter le projet).  
- Un compte émetteur Google Wallet (Issuer) — en mode démo ou production selon usage.  
- Clé de service Google Cloud pour signer les jetons JWT.  

## 🔧 Installation & utilisation

```bash
# 1. Cloner le dépôt
git clone https://github.com/Cornelliah/google_wallet_api_stutend_card.git
cd google_wallet_api_stutend_card

# 2. Construire le projet avec Maven
mvn clean package

# 3. Configurer les variables nécessaires
# – définir l’issuer ID
# – fournir la clé de service (service account) pour signer les JWT

# 4. Exécuter le service backend
mvn exec:java -Dexec.mainClass="path.to.Main"  # adapter selon ta classe main

# 5. Obtenir le JWT généré
# 6. Dans ton application cliente (mobile / web), soumettre le JWT pour ajouter la carte à Google Wallet

