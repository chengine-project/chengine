---
layout: default
title: Подготовка релиза
section: РАЗРАБОТКА
permalink: /releasing/
lead: Проверка сборки и локальной публикации библиотек перед выпуском.
---

CI собирает проект и запускает тесты на JDK 17 и 21. Интеграционные тесты сессий используют отдельную тестовую базу PostgreSQL. Затем CI публикует артефакты в изолированный локальный Maven-репозиторий и запускает отдельный проект-потребитель через Gradle-метаданные и Maven POM.

Для локальной проверки публикации без изменения основного Maven-репозитория:

```bash
RELEASE_TEST_REPO="$(mktemp -d)"
./gradlew clean build publishToMavenLocal -Dmaven.repo.local="$RELEASE_TEST_REPO"
CHENGINE_LOCAL_MAVEN="$RELEASE_TEST_REPO" ./gradlew -p verification/consumer clean run
CHENGINE_LOCAL_MAVEN="$RELEASE_TEST_REPO" CHENGINE_POM_ONLY=true ./gradlew -p verification/consumer clean run
```

Для выполнения интеграционных тестов PostgreSQL перед сборкой задайте `CHENGINE_TEST_POSTGRES_URL`, `CHENGINE_TEST_POSTGRES_USER` и `CHENGINE_TEST_POSTGRES_PASSWORD`. Тесты удаляют строки таблицы `chengine_sessions`, поэтому используйте отдельную тестовую базу. Без URL эти тесты пропускаются.

Публичная публикация пока не настроена. Перед первым выпуском необходимо выбрать Maven-репозиторий, подтвердить права на координаты `io.github.chengine-project`, настроить подпись артефактов и учётные данные, а также проверить требования выбранного репозитория к метаданным и документации. Версию задаёт корневой `build.gradle.kts`; лицензия MIT включается в JAR и указывается в POM.
