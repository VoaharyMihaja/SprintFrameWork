# SprintFrameWork

# README

Pour utiliser la bibliothèque `SprintFrameWork` :

1. Insérez `mg.itu.prom16.Forntcontroller` comme servlet dans le fichier 'web.xml' de votre projet Tomcat.
2. Dans le fichier `web.xml`, ajoutez le package des contrôleurs en tant que paramètre de la servlet et nommez ce paramètre `basePackageName`.
3. Pour déclarer un contrôleur, ajoutez l'annotation `mg.itu.prom16.annotation.Controller` à la classe correspondante.