# [Monster Trading Card Game](https://github.com/if23b011/mtcg-server)

## Anleitung zur Verwendung von Docker

1. **Docker installieren**

    - Installiere Docker mithilfe der [offiziellen Anleitung zur Docker-Installation.](https://docs.docker.com/get-docker/)

2. **PostgreSQL-Container starten**

    - Verwende den folgenden Befehl, um einen PostgreSQL-Container zu starten:

   ```sh
   docker run -d --rm --name postgresdb -e POSTGRES_USER=user1 -e POSTGRES_PASSWORD=Password123 -e POSTGRES_DB=mtcg -p 5432:5432 -v pgdata:/var/lib/postgresql/data postgres
   ```

   **Erklärung des Befehls:**
    - **docker run**: Startet einen neuen Docker-Container.
    - **-d**: Führt den Container im Hintergrund aus (detached mode).
    - **--rm**: Entfernt den Container automatisch, wenn er gestoppt wird.
    - **--name postgresdb**: Gibt dem Container den Namen `postgresdb`.
    - **-e POSTGRES_USER=user1**: Setzt den Datenbank-Benutzer auf `user1`.
    - **-e POSTGRES_PASSWORD=Password123**: Setzt das Passwort für den Benutzer auf `Password123`.
    - **-e POSTGRES_DB=mtcg**: Erstellt eine Datenbank mit dem Namen `mtcg`.
    - **-p 5432:5432**: Verbindet den lokalen Port 5432 mit dem Container-Port 5432, sodass auf die Datenbank von außen zugegriffen werden kann.
    - **-v pgdata:/var/lib/postgresql/data**: Erstellt ein Docker-Volume `pgdata` zur dauerhaften Speicherung der Datenbankdaten, sodass die Daten auch nach dem Neustart des Containers erhalten bleiben.


3. **Mit der Datenbank verbinden**

    - Verwende den folgenden Befehl, um auf die PostgreSQL-Datenbank zuzugreifen:

   ```sh
   docker exec -it postgresdb psql -U user1 -d mtcg
   ```

  - **docker exec -it**: Führt einen Befehl im laufenden Container aus.
  - **postgresdb**: Der Name des Containers.
  - **psql -U user1 -d mtcg**: Greift als Benutzer `user1` auf die Datenbank `mtcg` zu.

4. **Tabellen erstellen**
    - Führe im PostgreSQL-Client den folgenden Befehl aus, um die `users`-Tabelle zu erstellen:

   ```sql
   CREATE TABLE users (
       id SERIAL PRIMARY KEY,
       username VARCHAR(50) UNIQUE NOT NULL,
       password VARCHAR(255) NOT NULL,
       coins INT DEFAULT 20,
       token VARCHAR(255)
   );
   ```

    - **id**: Eindeutiger Primärschlüssel für jeden Benutzer.
    - **username**: Der Benutzername, der eindeutig und obligatorisch sein muss.
    - **password**: Das gehashte Passwort des Benutzers.
    - **coins**: Die Anzahl der Coins, die ein Benutzer besitzt (Standardwert: 20).
    - **token**: Der Authentifizierungs-Token des Benutzers.

## Schritt-für-Schritt-Anleitung zur Erstellung der `config.properties`

1. **Erstellen der Datei**

    - Erstelle eine neue Datei mit dem Namen `config.properties` im Ordner `src/main/resources`.

2. **Inhalt der `config.properties`**

    - Füge die folgenden Zeilen in die `config.properties`-Datei ein und passe die Werte entsprechend deiner Datenbankkonfiguration an:

   ```properties
   db.url=jdbc:postgresql://localhost:5432/mtcg
   db.user=user1
   db.password=Password123
   ```

    - **db.url**: Die URL der PostgreSQL-Datenbank. Diese sollte das Schema (`jdbc:postgresql://`), den Host (`localhost`), den Port (`5432`) und den Namen der Datenbank (`mtcg`) enthalten.
    - **db.user**: Der Benutzername, der verwendet wird, um sich bei der Datenbank anzumelden.
    - **db.password**: Das Passwort für den angegebenen Benutzer.
