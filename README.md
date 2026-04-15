# ChuckFaster - Plugin Minecraft 1.21.4

**Développé par SEBmyG - TBS Corps**

## Description

ChuckFaster est un plugin Minecraft qui permet d'accélérer la croissance des plantes dans un rayon d'un chunk en utilisant des blocs de magnétite alimentés par des diamants.

## Fonctionnalités

### Blocs de Magnétite
- **Matériau** : Lodestone (bloc de magnétite Minecraft)
- **Effet** : Accélère la croissance des plantes dans le chunk
- **Alimentation** : 1 bloc de diamant = +2.5% de vitesse de croissance
- **Capacité maximale** : 64 blocs de diamant par bloc (configurable)
- **Effet visuel** : Particules magiques et texte flottant indiquant le pourcentage (nécessite GHolo)

### Cultures affectées
- Blé, Carottes, Pommes de terre, Betteraves
- Canne à sucre, Cactus, Bambou
- Tous les types d'arbres (saplings)
- Champignons (normaux et du Nether)
- Nether Wart
- Kelp, Sea Pickle
- Sweet Berry Bush
- Cocoa

## Commandes

### `/chfast give <joueur> [quantité]`
- **Permission** : `chuckfaster.give`
- **Description** : Donne des blocs de magnétite à un joueur
- **Exemples** :
  - `/chfast give Steve` - Donne 1 bloc à Steve
  - `/chfast give Alex 5` - Donne 5 blocs à Alex

### `/chfast reload`
- **Permission** : `chuckfaster.admin`
- **Description** : Recharge la configuration du plugin

### `/chfast info`
- **Permission** : Aucune
- **Description** : Affiche les informations du plugin et du développeur

### `/chfast help`
- **Permission** : Aucune
- **Description** : Affiche l'aide des commandes

## Utilisation

### Placer un bloc de magnétite
1. Obtenez un bloc avec `/chfast give <joueur>`
2. Placez-le où vous voulez dans votre ferme
3. Le bloc affectera toutes les plantes du chunk

### Alimenter le bloc
**Méthode 1 - Clic direct :**
- Tenez des blocs de diamant dans votre main
- Clic droit sur le bloc de magnétite
- Les blocs de diamant seront automatiquement ajoutés

**Méthode 2 - Interface :**
- Shift + Clic droit sur le bloc
- Déposez les blocs de diamant dans l'interface (style coffre)
- Récupérez les blocs de diamant quand vous le souhaitez

### Récupérer le bloc
- **Shift + Clic gauche** sur le bloc : Récupère le bloc et tous les blocs de diamant stockés

### Consulter les informations
- **Clic gauche** sur le bloc : Affiche les statistiques dans le chat

## Permissions

### `chuckfaster.admin`
- **Par défaut** : OP
- **Description** : Accès à toutes les commandes administratives

### `chuckfaster.use`
- **Par défaut** : true
- **Description** : Permet d'utiliser les blocs de magnétite

### `chuckfaster.give`
- **Par défaut** : OP
- **Description** : Permet d'utiliser la commande give

## Configuration

Le fichier `config.yml` permet de personnaliser :

```yaml
magnetite:
  speed-increase-per-diamond: 2.5  # % d'augmentation par diamant
  max-diamonds-per-block: 64       # Nombre max de diamants par bloc
  chunk-radius: 1                  # Rayon d'effet (en chunks)
  
  particles:
    enabled: true                  # Activer les particules
    
  hologram:
    enabled: true                  # Activer le texte flottant

growth:
  base-chance: 0.1                 # Chance de base de croissance (0.0 à 1.0)
```

## Installation

1. Téléchargez le fichier `.jar` du plugin
2. Placez-le dans le dossier `plugins/` de votre serveur
3. **(Optionnel)** Installez [GHolo](https://www.spigotmc.org/resources/gholo.99984/) pour les hologrammes
4. Redémarrez le serveur
5. Le plugin génère automatiquement ses fichiers de configuration

### GHolo (Recommandé)
Pour afficher les hologrammes flottants au-dessus des blocs :
- Téléchargez GHolo depuis [SpigotMC](https://www.spigotmc.org/resources/gholo.99984/)
- Placez le dans `plugins/`
- Redémarrez le serveur
- Les hologrammes s'afficheront automatiquement !

## Compilation

```bash
mvn clean package
```

Le fichier `.jar` sera généré dans `target/chuckfaster-1.0.0.jar`

## Support

- **Développeur** : SEBmyG
- **Site web** : 
- **Contact (discord)** : sebmyg

## Version

- **Version actuelle** : 1.0.2
- **Compatibilité** : Minecraft 1.21.4
- **API** : Paper/Spigot
- **Hologrammes** : GHolo (optionnel)

---

© 2024 TBS Corps - Tous droits réservés
