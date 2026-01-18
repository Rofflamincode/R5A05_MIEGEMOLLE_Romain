# R5A.05_MIEGEMOLLE_Romain

# API Blog – R5A05 (Spring Boot)

Projet réalisé dans le cadre des TP Spring **R5A05** : une API HTTP pour gérer des **articles de blog**, avec **authentification JWT** et **autorisations** selon le rôle (**MODERATOR** / **PUBLISHER**).

## Stack

- Java **21**
- Spring Boot **3.5.6**
- Maven
- MySQL
- Spring Data JPA
- Spring Security + JWT (JJWT)

## Prérequis

- JDK 21
- MySQL en local
- Maven (ou Maven intégré à IntelliJ)
- Postman (ou curl)

## Installation

1. Cloner le dépôt et ouvrir le projet dans IntelliJ.
2. Installer les dépendances Maven :


mvn clean install -U
Base de données (MySQL)
Par défaut, l'application est configurée pour se connecter à :

Base : blog_api

Utilisateur : blog_user

Mot de passe : blog_pwd

Création rapide (à adapter à votre poste) :

sql
Copier le code
CREATE DATABASE IF NOT EXISTS blog_api CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'blog_user'@'localhost' IDENTIFIED BY 'blog_pwd';
GRANT ALL PRIVILEGES ON blog_api.* TO 'blog_user'@'localhost';
FLUSH PRIVILEGES;
Les paramètres sont dans : src/main/resources/application.properties

Lancer l’application
Depuis la racine du projet :

bash
Copier le code
mvn spring-boot:run
ou lancer R5A05MiegemolleRomainApplication depuis IntelliJ.

URL locale : http://localhost:8080

Tags Git attendus (livrables)
hello-world : endpoint GET /bonjour retourne "Bonjour le monde !"

db-ready : connexion MySQL fonctionnelle

authentification : obligation de s'authentifier pour les opérations sensibles

autorisations : restrictions selon rôle et identité

Authentification (JWT)
Obtenir un token
Requête : POST /auth/login

Body JSON :

json
Copier le code
{
  "username": "romain",
  "password": "motdepasse"
}
Réponse (JwtDTO) :

json
Copier le code
{
  "accessToken": "<JWT>",
  "tokenType": "Bearer",
  "expiresIn": 60000,
  "roles": ["PUBLISHER"]
}
Durée de vie du token
La durée de vie est actuellement 60 secondes (60000 ms) (valeur codée dans TokenGenerator : 1000 * 60).

Utiliser le token
Pour toutes les routes protégées, ajouter l’en-tête :

Key: Authorization

Value: Bearer <JWT>

Exemple :

makefile
Copier le code
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9....
Endpoints
Base URL : http://localhost:8080

Hello World (TP1)
GET /bonjour → Bonjour le monde !

Utilisateurs
POST /users : crée un utilisateur

Body JSON :

json
Copier le code
{
  "username": "romain",
  "password": "<hash-bcrypt>",
  "role": "PUBLISHER"
}
Remarque : pour que le login fonctionne, password doit être un hash BCrypt (voir section précédente).

Articles
GET /articles : liste des articles (public)

GET /articles/{id} : détail d’un article (public)

POST /articles : créer un article (PUBLISHER)

body :

json
Copier le code
{
  "content": "Mon contenu"
}
PUT /articles/{id} : modifier (auteur uniquement, PUBLISHER)

body :

json
Copier le code
{
  "content": "Nouveau contenu"
}
DELETE /articles/{id} : supprimer (MODERATOR ou auteur)

POST /articles/{id}/like : liker (PUBLISHER, pas auteur)

POST /articles/{id}/dislike : disliker (PUBLISHER, pas auteur)

Tests rapides (Postman)
1) Lister en public
GET http://localhost:8080/articles

Doit fonctionner sans token.

2) Créer un utilisateur (via SQL recommandé)
Les mots de passe doivent être en BCrypt.
Vous pouvez générer un hash BCrypt avec ce petit bout de code Java :

java
Copier le code
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptGen {
  public static void main(String[] args) {
    System.out.println(new BCryptPasswordEncoder().encode("motdepasse"));
  }
}
Puis insérer en SQL :

sql
Copier le code
INSERT INTO users(username, password, role)
VALUES ('romain', '<hash_bcrypt>', 'PUBLISHER');
3) Login
POST http://localhost:8080/auth/login

Copier le accessToken.

4) Créer un article (PUBLISHER)
POST http://localhost:8080/articles

Header Authorization: Bearer <token>

Body JSON { "content": "..." }

5) Like / Dislike
POST http://localhost:8080/articles/{id}/like

POST http://localhost:8080/articles/{id}/dislike

Autorisations (règles)
Rappels de l’implémentation actuelle :

Utilisateur non authentifié
Peut consulter les articles : GET /articles et GET /articles/{id}

Les informations renvoyées sont limitées : auteur, date de publication, contenu

Rôle PUBLISHER
Peut poster un article : POST /articles

Peut modifier / supprimer uniquement ses articles : PUT /articles/{id}, DELETE /articles/{id}

Peut liker/disliker uniquement les articles des autres : POST /articles/{id}/like|dislike

Rôle MODERATOR
Peut consulter tous les articles avec le maximum d’infos (y compris listes de likes/dislikes)

Peut supprimer n’importe quel article : DELETE /articles/{id}

Notes
La clé JWT (r5a05.app.jwtSecret) doit être en Base64 (sinon JJWT déclenche une erreur de décodage).

Le token expirant au bout de 60s, il faut parfois relancer un POST /auth/login pendant les tests.
