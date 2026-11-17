---
layout: default
lang: de
title: Sitzungen und Dialoge
section: FUNKTIONEN
permalink: /de/sessions/
lead: Schritt und Benutzerdaten zwischen Nachrichten speichern.
---

## Dialog starten

```kotlin
import io.chengine.connector.BotRequestContext
import io.chengine.session.Conversations

@HandleCommand("/start")
fun start(request: BotRequestContext) = sendTelegramMessage {
    Conversations.start(request, "name")
    text = "Wie heißen Sie?"
}
```

Zum Erstellen einer Sitzung muss das eingehende Ereignis Benutzer und Chat enthalten. Vor `start` kann `request.sessionKey()` geprüft werden.

## Antwort verarbeiten

```kotlin
@TelegramHandleText
fun answer(request: BotRequestContext) = sendTelegramMessage {
    val session = Conversations.current()
    val answer = request.textContent()?.trim().orEmpty()
    text = when {
        session == null -> "Beginnen Sie mit /start"
        session.step == "name" -> {
            session["name"] = answer
            session.step = "city"
            "In welcher Stadt sind Sie?"
        }
        session.step == "city" -> {
            val name = session["name"]
            Conversations.end()
            "Schön, Sie kennenzulernen, $name! Ihre Stadt: $answer."
        }
        else -> "Beginnen Sie mit /start"
    }
}
```

Rufe zum Abbrechen `Conversations.end()` im Handler für `/cancel` auf. Dieses Beispiel verwendet Sitzungen direkt; das [vollständige Beispiel](https://github.com/chengine-project/chengine/tree/develop/examples/conversation) verwendet einen Graphen und prüft leere Antworten sowie fehlende Sitzungsschlüssel.

## Speicherung und Grenzen

Standardmäßig werden Sitzungen im Arbeitsspeicher gehalten und gehen bei einem Neustart verloren. Das Inaktivitätslimit beträgt standardmäßig zehn Minuten und lässt sich über `timeout` und `unit` bei `Conversations.start` ändern.

Der Schlüssel umfasst Plattform, Bot-Konto, Benutzer und Chat. Bei Telegram wird der konfigurierte Bot-Benutzername verwendet; eine Änderung ändert deshalb die Schlüssel. Anfragen derselben Sitzung werden innerhalb einer JVM serialisiert; die Reihenfolge gleichzeitig eintreffender Nachrichten ist nicht garantiert.

## PostgreSQL

Das Modul `session-postgres` speichert `ConversationSession` über Neustarts hinweg. Füge `io.chengine:session-postgres:1.0.1-BETA` hinzu, konfiguriere eine PostgreSQL-`DataSource` und registriere eine Bean:

```kotlin
@Bean
fun sessionStore(dataSource: DataSource): SessionStore =
    PostgresSessionStore(dataSource).also { it.initializeSchema() }
```

Importe: `io.chengine.session.SessionStore`, `io.chengine.session.postgres.PostgresSessionStore`, `javax.sql.DataSource` und `org.springframework.context.annotation.Bean`. Im Produktivbetrieb kann die Tabelle auch getrennt beim Einrichten des Datenbankschemas erstellt werden. `deleteExpired()` entfernt abgelaufene Datensätze. Der Store unterstützt `ConversationSession`; für ein anderes Backend kann `SessionStore` implementiert werden.

PostgreSQL speichert den Zustand, synchronisiert aber keine mehreren Prozesse: Dafür ist eine separate Koordination nötig. Speichern und Senden einer Nachricht sind keine gemeinsame Transaktion.

## Dialoggraph

Der experimentelle `ConversationGraph` ergänzt benannte Knoten, bedingte Übergänge und das Warten auf eine Antwort auf Basis derselben Sitzung. [Mehr über Graphen →]({{ '/de/graphs/' | relative_url }})

[Vollständiges Beispiel im Repository](https://github.com/chengine-project/chengine/tree/develop/examples/conversation)
