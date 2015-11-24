Généralités sur les réseaux, Routage, IP, UDP

1 Préambule

Le but TP est le suivant :

— S’initier à la programmation Client/Serveur en utilisant le protocole UDP
— Illustrer les limites de ce protocole
— Rappeler certains concepts de base de la programmation Java (principalement l’héritage et la créa-tion de threads).

2 Serveur et Client Echo

Le but de cet exercice est d’implémenter un serveur et un client remplissant la fonctionalité Echo :

— Le serveur attends des datagrammes UDP contenant de simple messages texte, sur le port 12345.
— Sur reception d’un datagramme, le serveur affiche le contenu de celui-ci dans la console.
— Le client se connecte à un serveur sur le port UDP 12345 d’un serveur.
— L’utilisateur ayant lancé le client peut écrire des messages sur l’entrée standard (dans la console) qui sont envoyé au serveur.

Un tel couple Client/Serveur peut être vu comme un squelette d’application de messagerie instantanée.
