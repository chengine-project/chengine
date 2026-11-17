# Документация и GitHub Pages

Содержимое этой папки — Jekyll-сайт для GitHub Pages. После слияния в `develop` откройте **Settings → Pages → Build and deployment**, выберите **Deploy from a branch**, затем ветку `develop` и папку `/docs`, и сохраните настройки. GitHub соберёт и опубликует сайт.

Ожидаемый адрес для стандартного домена организации: `https://chengine-project.github.io/chengine/`. Фактический адрес отображается в настройках Pages после публикации. При смене имени репозитория или использовании другого пути обновите `baseurl` в `_config.yml`.

Для локальной проверки с установленным Jekyll:

```bash
jekyll serve --source docs --baseurl ""
```

Страницы написаны в Markdown, общее оформление находится в `_layouts/default.html` и `assets/style.css`.
