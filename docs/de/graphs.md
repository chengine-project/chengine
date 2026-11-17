---
layout: default
lang: de
title: Graphen und Experimente
section: FUNKTIONEN
permalink: /de/graphs/
lead: Dialoge aus Knoten, Übergängen und Wartepunkten für Nachrichten aufbauen.
---

`ConversationGraph` ist ein experimenteller synchroner Graph auf Basis von `ConversationSession`. Ein Knoten erhält den Zustand und eingehenden Text und gibt einen Übergang zurück:

- `Next("node")` führt sofort den nächsten Knoten aus;
- `Await("node", "Nachricht")` gibt eine Nachricht zurück und wartet auf neuen Text für den angegebenen Knoten;
- `Finish("Nachricht")` beendet den Ablauf.

```kotlin
val graph = ConversationGraph("start", mapOf(
    "start" to { _, _ -> GraphTransition.Await("name", "Wie heißen Sie?") },
    "name" to { session, input ->
        session["name"] = input.orEmpty()
        GraphTransition.Next("finish")
    },
    "finish" to { session, _ -> GraphTransition.Finish("Hallo, ${session["name"]}!") }
))
```

Erstelle im Handler eine Sitzung mit `Conversations.start`, rufe `graph.start(session)` auf und gib den Nachrichtentext des Ergebnisses zurück. Bei der nächsten Nachricht rufe `graph.resume(session, text)` auf. Wenn `result.completed` gleich `true` ist, beende die Sitzung mit `Conversations.end()`. Übergib Daten zwischen automatischen Knoten über die Sitzung: Eingabetext wird bei `Next` nicht weitergegeben. Ein Übergangslimit verhindert Endlosschleifen.

[Vollständiges Beispiel mit Name, Stadt und Verzweigung](https://github.com/chengine-project/chengine/tree/develop/examples/conversation).

## Werkzeug für Agenten

Das experimentelle `TelegramSendMessageTool` bindet sich an den Chat der aktuellen Anfrage und sendet Text über den übergebenen Telegram-Client. Das Modell liefert den Text, der Chat kommt aus dem Kontext. Im Projekt gibt es einen Test mit einem simulierten Modell; ein fertiger LLM-Adapter oder eine Agentenschleife existiert noch nicht. [Beschreibung und Grenzen](https://github.com/chengine-project/chengine/tree/develop/examples/agent-tool).
