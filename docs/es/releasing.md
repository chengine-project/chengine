---
layout: default
lang: es
title: Preparación de una versión
section: DESARROLLO
permalink: /es/releasing/
lead: Comprueba la compilación y publicación local de las bibliotecas antes del lanzamiento.
---

CI compila el proyecto y ejecuta las pruebas en JDK 17 y 21. Las pruebas de integración de sesiones usan una base PostgreSQL de prueba independiente. Después, CI publica los artefactos en un repositorio Maven local aislado y ejecuta un proyecto consumidor independiente con los metadatos de Gradle y el POM de Maven.

Para verificar la publicación local sin modificar el repositorio Maven principal:

```bash
RELEASE_TEST_REPO="$(mktemp -d)"
./gradlew clean build publishToMavenLocal -Dmaven.repo.local="$RELEASE_TEST_REPO"
CHENGINE_LOCAL_MAVEN="$RELEASE_TEST_REPO" ./gradlew -p verification/consumer clean run
CHENGINE_LOCAL_MAVEN="$RELEASE_TEST_REPO" CHENGINE_POM_ONLY=true ./gradlew -p verification/consumer clean run
```

Para ejecutar las pruebas de integración PostgreSQL, define antes de compilar `CHENGINE_TEST_POSTGRES_URL`, `CHENGINE_TEST_POSTGRES_USER` y `CHENGINE_TEST_POSTGRES_PASSWORD`. Las pruebas eliminan las filas de la tabla `chengine_sessions`, así que usa una base de prueba independiente. Sin la URL se omiten estas pruebas.

La publicación pública aún no está configurada. Antes del primer lanzamiento hay que elegir un repositorio Maven, confirmar los derechos sobre las coordenadas `io.github.chengine-project`, configurar la firma de artefactos y las credenciales, y comprobar los requisitos del repositorio elegido en cuanto a metadatos y documentación. La versión se establece en el `build.gradle.kts` raíz; la licencia MIT se incluye en el JAR y se indica en el POM.
