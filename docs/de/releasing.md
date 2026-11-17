---
layout: default
lang: de
title: Release vorbereiten
section: ENTWICKLUNG
permalink: /de/releasing/
lead: Build und lokale Veröffentlichung von Bibliotheken vor einem Release prüfen.
---

CI baut und testet das Projekt mit JDK 17 und 21. Integrationstests für Sitzungen nutzen eine separate PostgreSQL-Testdatenbank. Anschließend veröffentlicht CI Artefakte in ein isoliertes lokales Maven-Repository und startet ein separates Consumer-Projekt über Gradle-Metadaten und Maven-POM.

Für eine lokale Prüfung ohne Änderung des regulären Maven-Repositorys:

```bash
RELEASE_TEST_REPO="$(mktemp -d)"
./gradlew clean build publishToMavenLocal -Dmaven.repo.local="$RELEASE_TEST_REPO"
CHENGINE_LOCAL_MAVEN="$RELEASE_TEST_REPO" ./gradlew -p verification/consumer clean run
CHENGINE_LOCAL_MAVEN="$RELEASE_TEST_REPO" CHENGINE_POM_ONLY=true ./gradlew -p verification/consumer clean run
```

Setze vor dem Build für die PostgreSQL-Integrationstests `CHENGINE_TEST_POSTGRES_URL`, `CHENGINE_TEST_POSTGRES_USER` und `CHENGINE_TEST_POSTGRES_PASSWORD`. Die Tests löschen Zeilen aus `chengine_sessions`; verwende daher eine eigene Testdatenbank. Ohne URL werden diese Tests übersprungen.

Eine öffentliche Veröffentlichung ist noch nicht eingerichtet. Vor dem ersten Release müssen ein Maven-Repository gewählt, die Rechte an den Koordinaten `io.github.chengine-project` bestätigt, Artefaktsignierung und Zugangsdaten eingerichtet und die Metadaten- und Dokumentationsanforderungen des Repositorys geprüft werden. Die Version steht in der zentralen `build.gradle.kts`; die MIT-Lizenz ist im JAR enthalten und im POM angegeben.

Für die lokale Vorbereitung und Prüfung des signierten Pakets ohne Upload siehe die [Release-Anleitung](https://github.com/chengine-project/chengine/blob/main/verification/RELEASE.md).
