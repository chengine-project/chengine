---
layout: default
lang: de
title: Bots einfacher erstellen
section: ÜBERBLICK
permalink: /de/
lead: Chengine ist ein Kotlin-Framework für Telegram-Bots mit vertrauten Handlern und Spring-Boot-Integration.
---

Vom ersten Befehl bis zum mehrstufigen Dialog: Beschreibe das Verhalten des Bots in Kotlin. Chengine findet den Handler und sendet die Antwort an Telegram.

<a class="button" href="{{ '/de/start/' | relative_url }}">Loslegen →</a><a class="button secondary" href="https://github.com/chengine-project/chengine">Auf GitHub ansehen ↗</a>

## Im Überblick

<div class="cards">
  <a class="card" href="{{ '/de/handlers/' | relative_url }}"><strong>Befehle und Text →</strong><span>Eingehende Ereignisse Methoden zuordnen.</span></a>
  <a class="card" href="{{ '/de/buttons/' | relative_url }}"><strong>Buttons und Callbacks →</strong><span>Tastaturen erstellen und Klicks verarbeiten.</span></a>
  <a class="card" href="{{ '/de/media/' | relative_url }}"><strong>Medien und Dateien →</strong><span>Fotos, Dateien und Videos senden und empfangen.</span></a>
  <a class="card" href="{{ '/de/sessions/' | relative_url }}"><strong>Sitzungen und Dialoge →</strong><span>Status im Speicher oder in PostgreSQL halten.</span></a>
  <a class="card" href="{{ '/de/polls/' | relative_url }}"><strong>Umfragen →</strong><span>Abstimmungen durchführen und Antworten verarbeiten.</span></a>
  <a class="card" href="{{ '/de/graphs/' | relative_url }}"><strong>Graphen →</strong><span>Abläufe mit Übergängen und Wartepunkten erstellen.</span></a>
  <a class="card" href="{{ '/de/examples/' | relative_url }}"><strong>Vollständige Beispiele →</strong><span>Vollständige Handler in der Dokumentation.</span></a>
</div>

## Kurzes Beispiel

```kotlin
@Handler
@Component
class HelloHandler {
    @HandleCommand("/hello")
    fun hello() = sendTelegramMessage {
        text = "Hallo!"
    }
}
```

> Diese Dokumentation beschreibt den Branch `develop`. Die API wird weiterentwickelt; einige Funktionen sind experimentell.
