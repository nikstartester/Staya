# Staya

Android-приложение на Kotlin + Jetpack Compose с модульной архитектурой.

## Архитектура приложения

![Граф зависимостей модулей](.github/assets/architecture_graph.png)

### Слои и модули

Проект разделён на пять слоёв, каждый со своей зоной ответственности. Зависимости строго однонаправленные: **feature → domain (опционально) → data → core**. Циклические зависимости между модулями запрещены.

---

### <img src=".github/assets/dot_purple.png" width="12"/> Feature-слой

Каждый экран приложения — отдельный Gradle-модуль. Внутри: Compose UI, ViewModel и, при необходимости, UseCase/Interactor.

```
:feature:profile/
    di/
        ProfileNavigationModule ← содержит @IntoSet провайдер EntryBuilder для подключения роутов навигации из ProfileNavigation в модуль `:app`.
    navigation/
        ProfileNavigation.kt    ← содержит функцию для регистрирации navigation entries для модуля
    ui/
        ProfileScreen.kt
        ProfileViewModel.kt
    domain/                     ← только если UseCase не переиспользуется
        GetProfileUseCase.kt
```

Feature-модули зависят от `:core:navigation_api` и могут зависеть от `:core:design`, `:core:models`, `:core:common`, а также от нужных `:data:*` или `:domain:*` модулей.

Feature-модули **не знают друг о друге** — переходы между экранами осуществляются через route-константы из `:core:navigation_api`.

UseCase как правило необходим, но если он дублирует вызов функций единственного репозитория, то от него можно откзаться и использовать репозиторий напрямую.

---

### <img src=".github/assets/dot_pink.png" width="12"/> Domain-слой — опциональный

Domain-модуль создаётся **только** при выполнении одного из условий:

1. **UseCase переиспользуется** в нескольких feature-модулях. Если UseCase нужен только одному экрану — он живёт внутри feature-модуля в пакете `domain/`.

2. **Несколько UseCase нужно объединить в Interactor.** Interactor — это класс, который комбинирует вызовы нескольких UseCase или репозиториев в единый сценарий. Если Interactor используется одной фичей — он остаётся в feature-модуле. Если несколькими — выносится в domain-модуль.

```
:domain:profile/
    GetProfileUseCase.kt           ← переиспользуется в :feature:profile и :feature:settings
    UpdateProfileUseCase.kt        ← переиспользуется в :feature:profile и :feature:settings
    ProfileInteractor.kt           ← комбинирует UseCase в единый сценарий
```

Domain-модули зависят от: `:data:*` (один или несколько), а также могут зависеть `:core:models`, `:core:common`.

Если UseCase просто прокидывает вызов в репозиторий без дополнительной логики — его не нужно создавать. ViewModel может обращаться к репозиторию напрямую через data-модуль. Но это на усмотрение.

---

### <img src=".github/assets/dot_teal.png" width="12"/> Data-слой

Каждая доменная сущность, которая требует работы с сетью или БД, получает свой data-модуль. Модуль содержит Repository и **максимум два** DataSource: Remote и Local.

```
:data:profile/
    ProfileRepository.kt           ← единый источник правды для фич
    ProfileRemoteDataSource.kt     ← запросы к серверу, работает с DTO
    ProfileLocalDataSource.kt      ← только если есть логика сверх DAO
    ProfileMapper.kt               ← DTO → Entity → Domain model
```

**Remote DataSource** — запросы к серверу через `HttpClient`. Принимает и возвращает DTO.

**Local DataSource** — как правило, отдельный класс не нужен: локальным источником служит DAO из `:core:database`, и Repository использует его напрямую. Класс создаётся, только если появляется логика сверх DAO: файлы рядом с БД, транзакция через несколько DAO и т.п. Прокси, который один в один повторяет методы DAO, не создаём.

Название DataSource выбрано, чтобы не путать архитектурные классы с библиотекой androidx DataStore.

Структура плоская — при 3–4 файлах подпапки `remote/` и `local/` избыточны.

Data-модули зависят от: `:core:network`, `:core:database`, `:core:models`, `:core:common`.

Не каждый data-модуль обязан иметь оба DataSource. Например, `:data:auth` работает только с Remote и `TokenStorage`, без локального кеша.

Интерфейсы для Repository и DataSource не используются — считаем, это лишний слой абстракции. Создавать интерфейсы только при реальной необходимости.

Правила работы Repository с БД — в разделе [Локальная база данных](#локальная-база-данных).

---

### Core-слой

Core-модули делятся на три группы по области применения.

#### <img src=".github/assets/dot_green.png" width="12"/> UI Shared Core — только для feature-слоя

**`:core:navigation_api`** — route-константы, аргументы навигации, навигационные контракты. Позволяет feature-модулям ссылаться на экраны друг друга без прямых зависимостей. Реализация навигации живёт в `:app` модуле.

**`:core:design`** — дизайн-система: тема, цвета, типографика, переиспользуемые Compose-компоненты. Все UI-элементы, общие для нескольких экранов.

#### <img src=".github/assets/dot_blue.png" width="12"/> Shared Core — для feature, domain и data слоёв

**`:core:models`** — чистые доменные модели: `data class`, `sealed class`, `enum`. Никаких аннотаций Room, Ktor, сериализации. Ноль зависимостей на другие модули.

**`:core:common`** — общие утилиты: расширения, хелперы, обёртки. Должен оставаться чистым Kotlin-модулем без Android-зависимостей. Если утилита нужна только одному слою — она живёт в этом слое, а не в common.

#### <img src=".github/assets/dot_amber.png" width="12"/> Infra Core — только для data-слоя

**`:core:network`** — экземпляр HTTP-клиента (Ktor), `TokenStorage` для хранения авторизационных токенов, `AuthInterceptor` для автоматической подстановки `Bearer`-заголовка, `ApiResponse` как обёртка результата, `safeApiCall` для единообразной обработки ошибок.

**`:core:database`** — экземпляр Room БД, все DAO и Entity. DAO и Entity вынуждены жить здесь из-за технического ограничения Room: БД должна знать обо всех таблицах при создании. Data-модули получают нужный DAO через DI. Подробнее — в разделе [Локальная база данных](#локальная-база-данных).

---

### Локальная база данных

Room 3 (`androidx.room3`) с драйвером `AndroidSQLiteDriver`. Одна БД `staya.db` на всё приложение.

#### Структура `:core:database`

```
:core:database/
    schemas/                   ← JSON-схемы Room, коммитятся в git
    StayaDatabase.kt
    di/DatabaseModule.kt       ← @Singleton БД + @Provides для каждого DAO
    pet/                       ← пакет на каждый домен
        Pet*Entity.kt
        PetDao.kt
```

- Один DAO на агрегат: например, `PetDao` работает с питомцами и их дочерними таблицами. Транзакции внутри агрегата — `@Transaction`-методы DAO.
- DAO домена X используется только в `:data:X`.
- Функции DAO — `suspend` или возвращают `Flow`.

#### Entity

- Поля Entity — только примитивы и строки. Enum-значения (порода, интересы, пол, статус) хранятся строковыми кодами и маппятся в enum в data-модуле через `fromCodeOrNull`. Так `:core:database` не зависит от `:core:models`, а удалённая или переименованная константа не ломает чтение старых строк.
- Entity не выходит за пределы data-модуля: наружу отдаются только модели из `:core:models`.

#### Repository как единый источник правды

- **Чтение — только из БД.** `observe*()` возвращает `Flow` доменных моделей с `distinctUntilChanged()`.
- **Обновление.** `refresh*()` — `suspend`-функция: загружает данные с сервера и в одной транзакции записывает их в БД. Ничего не возвращает, результат приходит через `Flow`. При ошибке бросает `ApiException`.
- **Синхронизация списка — полная:** в одной транзакции upsert пришедших записей и удаление отсутствующих. 404 на детальный запрос — удаление записи локально.
- **Запись** (создание, изменение) — сначала сервер, затем ответ сервера сохраняется в БД.
- **Маппинг:** DTO → Entity напрямую, без промежуточной доменной модели, чтобы не терять сырые коды и серверные поля. Entity → Domain — при чтении.
- **Свежесть данных:** кеш показывается сразу, обновление запускается в фоне при открытии экрана и по pull-to-refresh.
- **Состояния загрузки и ошибки ведёт ViewModel:** кеша нет и обновление упало — заглушка и snackbar; кеш есть — данные и snackbar.

#### Очистка

БД содержит данные конкретного пользователя с конкретного стенда. `:app` очищает все таблицы:
- при завершении сессии — `AuthStateProvider.isAuthorized` переходит из `true` в `false`;
- при смене стенда.

#### SQL

- В `@Query` используется только SQL, который поддерживает SQLite 3.18 — системная версия на minSdk 26. Room проверяет запросы при компиляции на более новом SQLite, поэтому, например, оконные функции (`OVER`), `RETURNING`, синтаксис `UPSERT` и `DROP COLUMN` скомпилируются, но упадут на старых устройствах.
- Для вставки с обновлением — `@Upsert`, а не `@Insert(onConflict = REPLACE)`: `REPLACE` удаляет строку и вставляет заново, из-за чего каскадно удаляются дочерние записи.

#### Миграции

- Схема экспортируется в `:core:database/schemas/`, файлы коммитятся.
- До первого релиза: `version = 1`, схема меняется свободно; после изменения схемы приложение переустанавливается.
- После релиза: любое изменение Entity — это `version + 1` и `AutoMigration` (или ручная `Migration`), покрытая тестом через `MigrationTestHelper`.
- `fallbackToDestructiveMigration` в релизе не используется. Допустим только `fallbackToDestructiveMigrationOnDowngrade`.

#### Драйвер

`AndroidSQLiteDriver` использует системный SQLite.

Переход на `BundledSQLiteDriver` будет, если понадобится KMP или новые фичи SQL.

---

### Граф зависимостей

```
:feature:*       → :domain:*  (опционально)
:feature:*       → :data:*    (напрямую, если без domain)
:feature:*       → :core:navigation_api, :core:design, :core:models, :core:common

:domain:*        → :data:*
:domain:*        → :core:models, :core:common

:data:*          → :core:network, :core:database
:data:*          → :core:models, :core:common

:core:models     → (ничего)
:core:common     → (ничего)
```

---

### Когда создавать domain-модуль: чек-лист

| Ситуация | Где живёт логика |
|---|---|
| UseCase нужен одному экрану, логика простая | Напрямую в ViewModel без UseCase |
| UseCase нужен одному экрану, логика сложная | `domain/` пакет внутри feature-модуля |
| UseCase нужен нескольким экранам | Отдельный `:domain:*` модуль |
| Несколько UseCase комбинируются в сценарий для одного экрана | Interactor внутри feature-модуля |
| Несколько UseCase комбинируются в сценарий для нескольких экранов | Interactor внутри `:domain:*` модуля |
