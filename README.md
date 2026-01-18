R5A05 – TP Spring Boot : API Blog (RPC HTTP) + JWT + Autorisations
1. Contexte et objectif

Ce projet met en place une API HTTP (style RPC) permettant de gérer des articles de blog :

Authentification via JSON Web Token (JWT)

Gestion des articles : création, consultation, modification, suppression

Gestion des réactions : like / dislike (en enregistrant quels utilisateurs ont liké/disliké)

Application d’autorisations selon rôle :

MODERATOR

PUBLISHER

Non authentifié

Le projet est réalisé avec Spring Boot, Spring Data JPA, MySQL, Spring Security.

2. Technologies

Java 21

Spring Boot 3.5.6

Maven

MySQL

Spring Data JPA

Spring Security + JWT (JJWT)

3. Rendu attendu et tags Git
TP1 – Initialisation

Tag : hello-world

Preuve : l’application démarre et un endpoint renvoie “Bonjour le monde !” à l’adresse http://localhost:8080/bonjour

TP2 – Implémentation

Tag : db-ready

Preuve : connexion MySQL fonctionnelle et application démarrable.

Un commit par fonctionnalité ajoutée (CRUD articles + like/dislike + utilisateurs si présents)

TP3 – Sécurisation

Tag : authentification

Preuve : ajout de l’obligation de s’authentifier pour les fonctionnalités sensibles, JWT opérationnel.

Tag : autorisations

Preuve : adaptation des comportements et autorisations selon identité + rôles.

4. Mise en place base de données (TP2)
Configuration (application.properties)

L’application utilise une base MySQL. Exemple de configuration :

spring.datasource.url=jdbc:mysql://localhost:3306/blog_api?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=blog_user
spring.datasource.password=blog_pwd

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080

Principe

La base “blog_api” doit exister.

Un utilisateur “blog_user” est dédié au projet.

Les tables sont générées automatiquement par JPA au lancement (ddl-auto=update).

5. Fonctionnalités API (TP2)
Articles

Création : POST /articles

Liste : GET /articles

Détail : GET /articles/{id}

Modification : PUT /articles/{id}

Suppression : DELETE /articles/{id}

Réactions

Like : POST /articles/{id}/like

Dislike : POST /articles/{id}/dislike

Objectif : tracer quels utilisateurs ont liké/disliké via des relations JPA (tables de jointure).

6. Authentification JWT (TP3 – Tag authentification)
Route de login

URL : /auth/login

Méthode : POST

Body JSON attendu (exemple) :

{
"username": "romain",
"password": "motDePasseEnClair"
}

Réponse attendue (JwtDTO)

Réponse contenant un token (Bearer) à envoyer ensuite dans le header Authorization.

Utilisation du token

Pour appeler une route protégée, ajouter un header :

Authorization: Bearer <TOKEN>

Durée de vie du token

Dans ce projet, la durée est de 60 secondes (60000 ms).
Cela vient de la configuration du TokenGenerator (valeur 1000*60).
Donc en tests Postman, si le token expire il faut relancer /auth/login.

7. Autorisations (TP3 – Tag autorisations)

Les règles suivantes sont appliquées.

Utilisateur non authentifié

Peut :

consulter les articles existants via GET /articles et GET /articles/{id}

Données accessibles :

auteur

date de publication

contenu

Utilisateur authentifié PUBLISHER

Peut :

poster un nouvel article (POST /articles)

consulter les articles publiés (GET)

modifier ses articles (PUT uniquement si auteur)

supprimer ses articles (DELETE uniquement si auteur)

liker/disliker les articles des autres (interdit sur ses propres articles)

Données accessibles sur un article :

auteur

date

contenu

nombre total de likes

nombre total de dislikes

Utilisateur authentifié MODERATOR

Peut :

consulter n’importe quel article avec toutes les infos :

auteur

date

contenu

liste des utilisateurs ayant liké

total likes

liste des utilisateurs ayant disliké

total dislikes

supprimer n’importe quel article

8. Procédure de test (Postman) – Preuves de conformité
A. Vérifier accès public (non authentifié)

Appeler GET http://localhost:8080/articles
 sans header Authorization
Attendu : 200 OK
Attendu : réponse limitée (auteur, date, contenu)

Appeler GET http://localhost:8080/articles/1
 sans token
Attendu : 200 OK

B. Se connecter (JWT)

Appeler POST http://localhost:8080/auth/login

Body JSON exemple :

{
"username": "romain",
"password": "motDePasseEnClair"
}

Attendu : 200 OK
Attendu : token renvoyé

C. Tester routes protégées sans token

POST http://localhost:8080/articles
 sans token
Attendu : 401 Unauthorized

D. Tests PUBLISHER

Avec token PUBLISHER :

POST /articles
Attendu : OK (article créé)

Modifier un article dont on est auteur :

PUT /articles/{id} avec token PUBLISHER auteur
Attendu : 200 OK

Modifier un article dont on n’est pas auteur :

PUT /articles/{id} avec token PUBLISHER non auteur
Attendu : 403 Forbidden

Like/dislike :

POST /articles/{id}/like avec token PUBLISHER sur article d’un autre
Attendu : 200 OK

#POST /articles/{id}/like sur son propre article
Attendu : 403 Forbidden

Même logique pour dislike.

E. Tests MODERATOR

GET /articles avec token MODERATOR
Attendu : réponse complète (listes utilisateurs likes/dislikes + totaux)

DELETE /articles/{id} avec token MODERATOR
Attendu : 204 No Content même si pas auteur

9. Données utilisateurs (exemple)

Les utilisateurs sont stockés dans la table users.
Chaque utilisateur a :

username

password (hash BCrypt)

role (PUBLISHER ou MODERATOR)

Exemple de vérification en base :

SELECT * FROM users;

10. Conclusion

Les exigences des trois TP sont respectées :

TP1 : application initialisée, endpoint /bonjour disponible, tag hello-world

TP2 : base MySQL configurée et opérationnelle, CRUD articles + réactions, tag db-ready

TP3 : authentification JWT fonctionnelle, autorisations par rôle et identité, tags authentification et autorisations
