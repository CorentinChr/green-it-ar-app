# SAE_05 - Application de Sensibilisation Environnementale pour Lunettes Connectées

## 📋 Description du Projet

Ce projet est une application Android développée dans le cadre de la **SAE 05** (Situation d'Apprentissage et d'Évaluation). L'application est conçue pour fonctionner sur les lunettes connectées **Vuzix Blade 2** et propose des fonctionnalités interactives de sensibilisation environnementale.

L'application utilise le système d'ActionMenu de Vuzix pour une navigation intuitive adaptée aux lunettes connectées.

## 🎯 Fonctionnalités Principales

### 1. Quiz Thématique (`QuizActivity`)
- Quiz interactif avec sélection de thèmes
- Questions récupérées depuis une API backend
- Affichage d'informations complémentaires après chaque bonne réponse
- Navigation via le pad tactile des lunettes

### 2. Scanner QR Code (`MainActivity` - module qrcode)
- Scan de codes QR et codes-barres (QR_CODE, CODE_128)
- Ouverture automatique des URLs dans un WebView intégré
- Feedback sonore lors du scan réussi

### 3. Quiz Habitudes (`QuizHabitudeActivity`)
- Quiz d'auto-évaluation sur les habitudes écologiques
- Système de score cumulatif (chaque réponse attribue des points)
- Trois réponses possibles par question avec scores différenciés
- Affichage du score final

### 4. Fiches d'Information (`FicheInfoActivity`)
- Affichage de fiches informatives sur l'environnement
- Navigation entre les fiches via le menu d'action
- Contenu récupéré dynamiquement depuis le backend

## 🏗️ Architecture Technique

### Technologies Utilisées
- **Langage** : Java
- **Plateforme** : Android (SDK 22 minimum, cible SDK 34)
- **Framework UI** : Vuzix HUD ActionMenu
- **Networking** : Retrofit 2.9.0 avec Gson Converter
- **Scanner** : Vuzix SDK Barcode

### Structure du Projet

```
app/src/main/java/devkit/blade/vuzix/com/sae_app/
├── BladeSampleApplication.java       # Application principale
├── center_content_template_activity.java  # Activité d'accueil avec menu
├── QuizActivity.java                 # Quiz thématique
├── QuizHabitudeActivity.java         # Quiz sur les habitudes
├── FicheInfoActivity.java            # Affichage des fiches info
├── Template_Widget.java              # Widget pour le launcher
├── Template_Widget_Update_Receiver.java  # Récepteur de mises à jour widget
├── model/
│   ├── QuizItem.java                 # Modèle pour le quiz thématique
│   ├── QuizHabitudesItem.java        # Modèle pour le quiz habitudes
│   └── FicheInfoItem.java            # Modèle pour les fiches info
├── qrcode/
│   ├── MainActivity.java             # Scanner QR Code
│   ├── WebViewActivity.java          # Affichage web après scan
│   ├── ScanResultFragment.java       # Fragment résultat de scan
│   ├── PermissionsFragment.java      # Gestion des permissions
│   └── ...
└── retrofit/
    ├── RetrofitClient.java           # Client Retrofit singleton
    ├── QuizApi.java                  # API pour le quiz thématique
    ├── QuizHabitudeApi.java          # API pour le quiz habitudes
    └── FicheApi.java                 # API pour les fiches info
```

### API Backend

L'application communique avec un backend hébergé sur Railway :
- **URL de base** : `https://backendsae-production.up.railway.app/`
- **Endpoints** :
  - `GET /quiz` - Récupère les questions du quiz thématique
  - `GET /quiz-habitude` - Récupère les questions du quiz habitudes
  - `GET /fiches` - Récupère les fiches d'information

## 🚀 Installation et Build

### Prérequis
- Android Studio (version récente recommandée)
- JDK 8 ou supérieur
- Gradle 8.x

### Build du Projet

```bash
# Cloner le repository
git clone https://github.com/CorentinChr/SAE_05.git
cd SAE_05

# Build avec Gradle
./gradlew build

# Générer l'APK de debug
./gradlew assembleDebug
```

### Installation sur Vuzix Blade 2

1. Connecter les lunettes Vuzix en mode développeur via USB ou WiFi
2. Installer l'APK avec ADB :
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 📱 Permissions Requises

L'application nécessite les permissions suivantes :
- `INTERNET` - Communication avec le backend
- `ACCESS_NETWORK_STATE` - Vérification de l'état du réseau
- `CAMERA` - Scanner QR Code

## 🔧 Dépendances

```gradle
// UI Vuzix
implementation 'com.vuzix:hud-actionmenu:2.9.1'
implementation 'com.vuzix:hud-resources:2.4.0'
implementation 'com.vuzix:sdk-barcode:master-SNAPSHOT'

// Networking
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// AndroidX
implementation 'androidx.appcompat:appcompat:1.7.0'
```

## 👥 Équipe

Projet réalisé dans le cadre de la SAE 05.

## 📄 Licence

Ce projet est développé à des fins éducatives dans le cadre d'une SAE.
