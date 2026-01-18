# R5A05 — API Blog Spring Boot (TP1 / TP2 / TP3)

## Sommaire
1. Objectif  
2. Stack technique  
3. Livrables attendus (tags Git)  
4. Contexte fonctionnel  
5. Configuration & lancement  
6. Données de test (SQL)  
7. Endpoints (API)  
8. JWT (utilisation + durée de vie)  
9. Tests Postman (validation TP)  
10. Vérifications MySQL  
11. Conclusion  

---

## 1) Objectif

Développer une API HTTP (style RPC) de gestion d’articles de blog avec :

- Authentification JWT
- CRUD Articles (création, lecture, modification, suppression)
- Like / Dislike avec traçabilité des utilisateurs ayant réagi
- Autorisations selon rôle : MODERATOR / PUBLISHER / Non authentifié

---

## 2) Stack technique

- Java 21
- Spring Boot 3.5.6
- Maven
- MySQL
- Spring Web / Spring Data JPA
- Spring Security + JWT (JJWT)

---

## 3) Livrables attendus (tags Git)

TP1  
- tag `hello-world` : endpoint `GET /bonjour` retourne "Bonjour le monde !"

TP2  
- tag `db-ready` : connexion DB (MySQL) + génération des tables via JPA  
- commits par fonctionnalité (CRUD articles, like/dislike, etc.)

TP3  
- tag `authentification` : obligation de s’authentifier pour accéder aux routes protégées (JWT)  
- tag `autorisations` : adaptation du comportement selon rôle + identité (publisher/moderator/public)

---

## 4) Contexte fonctionnel

### Fonctionnalités
- Authentification utilisateurs (JWT)
- Articles : publication, consultation, modification, suppression
- Like / Dislike : retrouver les utilisateurs ayant liké/disliké

### Autorisations

Non authentifié  
- Peut consulter les articles existants  
- Infos visibles : auteur, date de publication, contenu  

PUBLISHER  
- Peut poster un article  
- Peut consulter les articles (avec totaux like/dislike)  
- Peut modifier/supprimer uniquement ses articles  
- Peut liker/disliker uniquement les articles des autres  

MODERATOR  
- Peut consulter n’importe quel article avec informations complètes :  
  - auteur, date, contenu  
  - liste des utilisateurs ayant liké + total likes  
  - liste des utilisateurs ayant disliké + total dislikes  
- Peut supprimer n’importe quel article  

---

## 5) Configuration & lancement

### Pré-requis
- JDK 21
- MySQL lancé
- Maven

### Configuration (application.properties)
Fichier : `src/main/resources/application.properties`

Exemple :
spring.datasource.url=jdbc:mysql://localhost:3306/blog_api?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true  
spring.datasource.username=blog_user  
spring.datasource.password=blog_pwd  
spring.jpa.hibernate.ddl-auto=update  
spring.jpa.show-sql=true  
spring.jpa.properties.hibernate.format_sql=true  
server.port=8080  

### Lancer le projet
- IntelliJ : Run `R5A05MiegemolleRomainApplication`
- Maven : `mvn clean install -U` puis exécuter l’application

---

## 6) Données de test (SQL)

### 6.1 Créer la base + un utilisateur MySQL (optionnel)
CREATE DATABASE IF NOT EXISTS blog_api;  
CREATE USER IF NOT EXISTS 'blog_user'@'localhost' IDENTIFIED BY 'blog_pwd';  
GRANT ALL PRIVILEGES ON blog_api.* TO 'blog_user'@'localhost';  
FLUSH PRIVILEGES;  

### 6.2 Insérer des utilisateurs de test (2 publishers + 1 moderator)

Important :  
- Spring Security utilise BCrypt, donc les mots de passe doivent être stockés chiffrés (format BCrypt).  
- Ici, on réutilise le hash BCrypt déjà présent (celui qui marche déjà dans la table).  
- Donc ces comptes auront le même mot de passe en clair : celui correspondant à ce hash.

USE blog_api;  

INSERT INTO users (username, password, role) VALUES  
('publisher_test', '$2a$10$KgjEHBGpdyDBStO3haoTv.9l5FpDZEplyu5qaFhOcXbjzG.2T57Eq', 'PUBLISHER'),  
('alice',          '$2a$10$KgjEHBGpdyDBStO3haoTv.9l5FpDZEplyu5qaFhOcXbjzG.2T57Eq', 'PUBLISHER'),  
('moderator_test', '$2a$10$KgjEHBGpdyDBStO3haoTv.9l5FpDZEplyu5qaFhOcXbjzG.2T57Eq', 'MODERATOR');  

Vérification :  
SELECT id, username, role FROM users;

---

## 7) Endpoints (API)

### 7.1 Authentification
POST `/auth/login`

Body JSON :
{ "username": "alice", "password": "motDePasseEnClair" }

Réponse : JWT (Bearer)  
Le token est dans `token`.

### 7.2 Articles
POST `/articles` (PUBLISHER requis)  
GET `/articles` (public)  
GET `/articles/{id}` (public)  
PUT `/articles/{id}` (PUBLISHER auteur uniquement)  
DELETE `/articles/{id}` (MODERATOR ou auteur PUBLISHER)

Body JSON (création) :
{ "content": "Mon article" }

Body JSON (update) :
{ "content": "Contenu modifié" }

### 7.3 Réactions
POST `/articles/{id}/like` (PUBLISHER + pas auteur)  
POST `/articles/{id}/dislike` (PUBLISHER + pas auteur)

---

## 8) JWT : utilisation et durée de vie

### Header à fournir (routes protégées)
Authorization: Bearer <TOKEN>

### Durée de vie du token
Durée actuelle : 60 secondes (60000 ms).

---

## 9) Tests Postman (validation TP)

### 9.1 Tests rapides (manuel)
A — Accès public (non authentifié)  
- GET `http://localhost:8080/articles` → 200 OK  
- GET `http://localhost:8080/articles/1` → 200 OK  

B — Login JWT  
- POST `http://localhost:8080/auth/login` → 200 OK + token  

C — Routes protégées sans token  
- POST `/articles` sans Authorization → 401 Unauthorized  

D — Tests PUBLISHER  
- POST `/articles` → OK  
- PUT `/articles/{id}`  
  - auteur → OK  
  - non auteur → 403 Forbidden  
- POST `/articles/{id}/like` et `/dislike` sur article d’un autre → OK  
- Like/dislike sur son propre article → 403 Forbidden  

E — Tests MODERATOR  
- GET `/articles` : réponses complètes (listes + totaux)  
- DELETE n’importe quel article → 204 No Content  

### 9.2 Collection Postman (importable)
Une collection Postman JSON est fournie (à importer dans Postman) pour tester :  
- login (publisher + moderator)  
- create article  
- list (public / publisher / moderator)  
- like / dislike  
- delete  

---

## 10) Vérifications MySQL

Voir les users :  
USE blog_api;  
SELECT id, username, role FROM users;

Voir les tables :  
SHOW TABLES;

Voir les likes/dislikes (tables de jointure) :  
SELECT * FROM article_likes;  
SELECT * FROM article_dislikes;

---

## 11) Conclusion

- TP1 : initialisation + endpoint /bonjour → tag `hello-world`
- TP2 : DB + API CRUD + réactions → tag `db-ready`
- TP3 : JWT + autorisations rôle/identité → tags `authentification` et `autorisations`
