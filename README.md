# 🏥 HospitalSMA — Système Multi-Agents Hospitalier

<div align="center">

![Java](https://img.shields.io/badge/Java-11-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JADE](https://img.shields.io/badge/JADE-4.5-00897B?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-1565C0?style=for-the-badge&logo=java&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

**Système intelligent de gestion hospitalière basé sur une architecture Multi-Agents JADE**

*Master SIT & Big Data — Faculté des Sciences et Techniques de Tanger*
*Université Abdelmalek Essaâdi — 2024/2025*

</div>

---

## 📋 Table des Matières

- [À Propos](#-à-propos)
- [Architecture](#-architecture-multi-agents)
- [Technologies](#-technologies-utilisées)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration Base de Données](#-configuration-base-de-données)
- [Lancer le Projet](#-lancer-le-projet)
- [Fonctionnalités](#-fonctionnalités)
- [Dashboard](#-dashboard)
- [Les Agents](#-les-agents)
- [Workflow](#-workflow)
- [Structure du Projet](#-structure-du-projet)
- [Auteur](#-auteur)

---

## 📖 À Propos

**HospitalSMA** est une plateforme intelligente de gestion hospitalière basée sur une architecture **Système Multi-Agents (SMA)** utilisant la plateforme **JADE (Java Agent DEvelopment Framework)**.

Le système représente chaque service hospitalier par un **agent autonome** capable de prendre des décisions indépendantes et de collaborer avec les autres agents via des messages **ACL (Agent Communication Language)** conformes au standard **FIPA**.

### Problématique
> Dans un hôpital traditionnel, la gestion des patients est manuelle et centralisée, causant des retards dans le traitement des urgences et une mauvaise coordination entre les services.

### Solution
> Un système distribué et intelligent où 6 agents autonomes représentent chaque service hospitalier, collaborant automatiquement pour traiter les patients selon leur priorité.

---

## 🏗️ Architecture Multi-Agents

```
┌─────────────────────────────────────────────────────────────┐
│                   JADE MAIN CONTAINER                        │
│                                                              │
│  ┌─────────────┐   REQUEST    ┌─────────────┐               │
│  │PatientAgent │─────────────►│  RDVAgent   │               │
│  └─────────────┘              └──────┬──────┘               │
│                                      │                       │
│                       ┌──────────────┴──────────────┐       │
│                  [CRITIQUE]                     [NORMAL]     │
│                       │                              │       │
│              ┌────────▼────────┐          ┌─────────▼─────┐ │
│              │ EmergencyAgent  │          │  DoctorAgent  │ │
│              └────────┬────────┘          └─────────┬─────┘ │
│                       │                             │       │
│                       │ INFORM               INFORM │       │
│                       │                             │       │
│              ┌────────▼─────────────────────────────▼─────┐ │
│              │              AdminAgent                      │ │
│              └──────────────────┬───────────────────────── ┘ │
│                                 │                            │
│                        ┌────────▼────────┐                  │
│                        │ PharmacyAgent   │                  │
│                        └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
                                 │
                        ┌────────▼────────┐
                        │   MySQL DB       │
                        │  hospital_sma   │
                        └─────────────────┘
```

---

## 🛠️ Technologies Utilisées

| Technologie | Version | Rôle |
|---|---|---|
| **Java** | JDK 11+ | Langage de développement |
| **JADE** | 4.5 | Plateforme Multi-Agents |
| **MySQL** | 8.0+ | Base de données |
| **MySQL Connector/J** | 8.x | Connexion Java ↔ MySQL |
| **Swing** | Java SE | Interface graphique dashboard |
| **ACL FIPA** | Standard | Communication entre agents |

---

## 📦 Prérequis

Avant de lancer le projet, assurez-vous d'avoir installé :

- ✅ **Java JDK 11** ou supérieur
- ✅ **IntelliJ IDEA** (recommandé) ou Eclipse
- ✅ **MySQL Server 8.0** ou supérieur
- ✅ **MySQL Workbench** (optionnel, pour visualiser la BD)
- ✅ **JADE 4.5** (`jade.jar`)
- ✅ **MySQL Connector/J** (`mysql-connector-j-8.x.jar`)

### Téléchargements

| Outil | Lien |
|---|---|
| Java JDK 11 | https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html |
| JADE 4.5 | https://jade.tilab.com/download/jade/license/jade-download/ |
| MySQL | https://dev.mysql.com/downloads/mysql/ |
| MySQL Connector/J | https://dev.mysql.com/downloads/connector/j/ |

---

## ⚙️ Installation

### 1. Cloner le Repository

```bash
git clone https://github.com/Ayoub-lah/SMA.git
cd SMA
```

### 2. Ouvrir dans IntelliJ IDEA

```
File → Open → Sélectionner le dossier SMA
```

### 3. Ajouter les librairies JAR

```
File → Project Structure → Libraries → + (Add)
→ Ajouter jade.jar
→ Ajouter mysql-connector-j-8.x.jar
→ Apply → OK
```

**Chemin recommandé pour les JARs :**
```
SMA/
└── lib/
    ├── jade.jar
    └── mysql-connector-j-8.x.jar
```

---

## 🗄️ Configuration Base de Données

### 1. Créer la base de données

Ouvrir **MySQL Workbench** ou le terminal MySQL et exécuter :

```sql
-- Créer la base de données
CREATE DATABASE hospital_sma;

-- Utiliser la base
USE hospital_sma;

-- Créer la table patients
CREATE TABLE patients (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nom          VARCHAR(100)  NOT NULL,
    consultation VARCHAR(100)  NOT NULL,
    priorite     VARCHAR(50)   NOT NULL,
    diagnostic   VARCHAR(200)  DEFAULT 'N/A',
    created_at   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);
```

### 2. Configurer la connexion

Dans le fichier `src/database/DBConnection.java`, modifier si nécessaire :

```java
private static final String URL  =
    "jdbc:mysql://localhost:3306/hospital_sma" +
    "?useSSL=false&allowPublicKeyRetrieval=true" +
    "&serverTimezone=UTC&autoReconnect=true";

private static final String USER = "root";   // ← Votre utilisateur MySQL
private static final String PASS = "1234";   // ← Votre mot de passe MySQL
```

### 3. Vérifier la connexion

```sql
-- Vérifier que la table est créée
SHOW TABLES;

-- Vérifier la structure
DESCRIBE patients;
```

---

## 🚀 Lancer le Projet

### Étape 1 — Vérifier la configuration

```
✓ MySQL Server démarré
✓ Base hospital_sma créée
✓ Table patients créée
✓ jade.jar ajouté au classpath
✓ mysql-connector-j.jar ajouté au classpath
```

### Étape 2 — Lancer depuis IntelliJ

```
1. Ouvrir le fichier : src/main/Main.java
2. Clic droit → "Run Main.main()"
   OU
   Cliquer sur le bouton ▶ vert en haut
```

### Étape 3 — Vérifier le démarrage

Dans la console, vous devez voir :

```
╔══════════════════════════════════════════════════════╗
║          HospitalSMA — JADE Multi-Agent System       ║
║          FST Tanger — SIT & Big Data 2025            ║
╠══════════════════════════════════════════════════════╣
║  ✓ EmergencyAgent   démarré                          ║
║  ✓ DoctorAgent      démarré                          ║
║  ✓ RDVAgent         démarré                          ║
║  ✓ PharmacyAgent    démarré                          ║
║  ✓ AdminAgent       démarré                          ║
╠══════════════════════════════════════════════════════╣
║  STATUS : Système prêt — En attente de patients...   ║
╚══════════════════════════════════════════════════════╝
```

### Étape 4 — Interface JADE RMA

Une fenêtre **JADE RMA (Remote Monitoring Agent)** s'ouvre automatiquement montrant tous les agents actifs.

### Étape 5 — Dashboard HospitalSMA

Le **Dashboard Swing** s'ouvre avec :
- Les KPI Cards (Total, Critiques, Normaux)
- Les Charts (Bar + Donut)
- La liste des patients
- Les logs des agents en temps réel

---

## ✨ Fonctionnalités

### Dashboard
- 📊 **KPI Cards** — Total patients, cas critiques, cas normaux
- 📈 **Bar Chart** — Consultations par priorité et par service
- 🥧 **Donut Chart** — Répartition critique/normal en pourcentage
- ⚡ **Activité Récente** — 6 derniers patients traités
- 🔄 **Auto-refresh** — Mise à jour automatique toutes les 5 secondes
- 📋 **Sidebar Toggle** — Sidebar rétractable avec animation

### Gestion des Patients
- ➕ **Nouveau Patient** — Formulaire d'ajout avec validation
- 🔍 **Recherche** — Filtrage en temps réel par nom/consultation
- 📥 **Export CSV** — Export de tous les patients
- 🗑️ **Vider BD** — Reset de la base de données avec confirmation

### Système Multi-Agents
- 🚨 **Gestion Urgences** — Détection automatique cas CRITIQUE
- 🩺 **Diagnostic Intelligent** — 10 spécialités médicales supportées
- 💊 **Prescription Auto** — Médicament adapté à chaque consultation
- 📝 **Logs Colorés** — Suivi en temps réel de chaque agent

---

## 🖥️ Dashboard

### Vue d'ensemble
![Dashboard](screenshots/dashboard.png)

### Page Patients
![Patients](screenshots/patients.png)

### Logs Agents
![Logs](screenshots/logs.png)

---

## 🤖 Les Agents

### 👤 PatientAgent
- **Behaviour** : `OneShotBehaviour` (s'exécute une seule fois)
- **Rôle** : Envoie la demande médicale au RDVAgent
- **Message** : `REQUEST` avec nom, consultation, priorité

### 📋 RDVAgent
- **Behaviour** : `CyclicBehaviour` (écoute en permanence)
- **Rôle** : Analyse la priorité et redirige vers le bon service
- **Décision** : CRITIQUE → EmergencyAgent | NORMAL → DoctorAgent

### 🚨 EmergencyAgent
- **Behaviour** : `CyclicBehaviour`
- **Rôle** : Gère les urgences critiques
- **Actions** : Ambulance, notification médecin, information admin

### 🩺 DoctorAgent
- **Behaviour** : `CyclicBehaviour`
- **Rôle** : Établit le diagnostic et prescrit le médicament
- **Spécialités** : Cardiologie, Neurologie, Orthopédie, Pédiatrie, Dermatologie, Pneumologie, Gastroentérologie, Ophtalmologie, Urgence, Générale

### 💊 PharmacyAgent
- **Behaviour** : `CyclicBehaviour`
- **Rôle** : Reçoit l'ordonnance et prépare les médicaments

### 🔧 AdminAgent
- **Behaviour** : `CyclicBehaviour`
- **Rôle** : Sauvegarde le dossier patient dans MySQL
- **Parser** : Extrait dynamiquement nom, consultation, priorité, diagnostic

---

## 🔄 Workflow

### Cas CRITIQUE
```
Utilisateur → PatientAgent
    └──[REQUEST]──► RDVAgent
                      └──[CRITIQUE détecté]──► EmergencyAgent
                                                    ├── Ambulance envoyée
                                                    ├── Médecin notifié
                                                    └──[INFORM]──► AdminAgent
                                                                       └── INSERT MySQL ✓
```

### Cas NORMAL
```
Utilisateur → PatientAgent
    └──[REQUEST]──► RDVAgent
                      └──[NORMAL détecté]──► DoctorAgent
                                                ├── Diagnostic généré
                                                ├──[INFORM]──► PharmacyAgent
                                                │                 └── Médicament préparé
                                                └──[INFORM]──► AdminAgent
                                                                   └── INSERT MySQL ✓
```

---

## 📁 Structure du Projet

```
HospitalSMA/
│
├── src/
│   ├── agents/
│   │   ├── AdminAgent.java         # Sauvegarde MySQL + parsing dynamique
│   │   ├── DoctorAgent.java        # Diagnostic intelligent par spécialité
│   │   ├── EmergencyAgent.java     # Gestion urgences critiques
│   │   ├── PatientAgent.java       # Envoi demande médicale
│   │   ├── PharmacyAgent.java      # Préparation médicaments
│   │   └── RDVAgent.java           # Analyse priorité + redirection
│   │
│   ├── database/
│   │   └── DBConnection.java       # Connexion MySQL + closeQuietly
│   │
│   ├── gui/
│   │   ├── HospitalDashboard.java  # Fenêtre principale + sidebar toggle
│   │   ├── StatsPanel.java         # Dashboard KPI + Charts + auto-refresh
│   │   ├── PatientTablePanel.java  # Table patients + recherche + export
│   │   ├── PatientFormDialog.java  # Formulaire nouveau patient
│   │   ├── BarChartPanel.java      # Chart barres (consultation)
│   │   ├── DonutChartPanel.java    # Chart donut (répartition)
│   │   ├── ActivityPanel.java      # Activité récente agents
│   │   ├── LogPanel.java           # Logs colorés terminal style
│   │   └── RoundedBorder.java      # Utilitaire bordures arrondies
│   │
│   └── main/
│       └── Main.java               # Point d'entrée + sendPatient()
│
├── lib/
│   ├── jade.jar                    # JADE Platform
│   └── mysql-connector-j-8.x.jar  # MySQL Driver
│
├── .gitignore
└── README.md
```

---

## 🗄️ Structure Base de Données

```sql
TABLE patients
┌────┬──────────────┬──────────────┬──────────┬──────────────────────┬─────────────────────┐
│ id │ nom          │ consultation │ priorite │ diagnostic           │ created_at          │
├────┼──────────────┼──────────────┼──────────┼──────────────────────┼─────────────────────┤
│  1 │ Ayoub        │ Cardiologie  │ CRITIQUE │ Urgence critique...  │ 2025-05-15 20:44:23 │
│  2 │ Mohamed      │ Neurologie   │ NORMAL   │ Migraine chronique   │ 2025-05-15 20:45:10 │
│  3 │ Sara         │ Pédiatrie    │ NORMAL   │ Infection virale...  │ 2025-05-15 20:46:05 │
└────┴──────────────┴──────────────┴──────────┴──────────────────────┴─────────────────────┘
```

---

## 🩺 Diagnostics Supportés

| Spécialité | Diagnostic | Médicament |
|---|---|---|
| Cardiologie | Hypertension artérielle | Amlodipine 5mg |
| Neurologie | Migraine chronique | Sumatriptan 50mg |
| Orthopédie | Fracture — bilan radiologique | Ibuprofène 400mg |
| Pédiatrie | Infection virale infantile | Paracétamol Pédiatrique |
| Dermatologie | Dermatite atopique | Hydrocortisone crème 1% |
| Pneumologie | Bronchite aiguë | Amoxicilline 500mg |
| Gastroentérologie | Gastrite chronique | Oméprazole 20mg |
| Ophtalmologie | Myopie — correction optique | Collyre Vitamine A |
| Urgence | Trauma — bilan complet requis | Morphine 10mg IV |
| Générale | Consultation générale | Paracétamol 1000mg |

---

## 🐛 Problèmes Fréquents

### ❌ `ClassNotFoundException: com.mysql.cj.jdbc.Driver`
```
Solution : Ajouter mysql-connector-j-8.x.jar au classpath
→ File → Project Structure → Libraries → + → mysql-connector.jar
```

### ❌ `Connection refused` MySQL
```
Solution : Vérifier que MySQL Server est démarré
→ Services Windows → MySQL80 → Démarrer
OU
→ MySQL Workbench → Connexion → Test Connection
```

### ❌ Agent name already registered
```
Solution : Arrêter complètement le process JADE avant de relancer
→ Stop dans IntelliJ → Relancer Main.java
```

### ❌ Dashboard vide / Charts pas affichés
```
Solution : Cliquer "Actualiser" manuellement
→ Le dashboard se peuple après le 1er auto-refresh (5s)
```

### ❌ `NullPointerException` dans StatsPanel
```
Solution : Vérifier que la table patients existe dans MySQL
→ USE hospital_sma; SHOW TABLES;
```

---

## 👨‍💻 Auteur

<div align="center">

**Ayoub Lahlaibi**

Master SIT & Big Data
Faculté des Sciences et Techniques de Tanger (FST)
Université Abdelmalek Essaâdi

[![GitHub](https://img.shields.io/badge/GitHub-Ayoub--lah-181717?style=for-the-badge&logo=github)](https://github.com/Ayoub-lah)

</div>

---

## 📄 Licence

Ce projet est sous licence **MIT** — voir le fichier [LICENSE](LICENSE) pour plus de détails.

---

<div align="center">

**HospitalSMA** — Développé avec ❤️ dans le cadre du Master SIT & Big Data — FST Tanger 2025

</div>