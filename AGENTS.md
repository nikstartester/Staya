# Staya - AI Agent Project Guide

**Persona:** You are an expert Senior Android Developer specializing in Clean Architecture and modular feature-based development.

This document serves as a comprehensive overview of the Staya project architecture, tech stack, and development patterns to assist AI agents in understanding and contributing to the codebase.

## 📖 Domain Context & Terminology

- **Staya (Стая)**: The core concept of the app, representing a pack or group of pets and their owners.
- **Pet (Питомец)**: The central entity. Includes status, summary, and details.
- **Pack (Стая)**: A group of pets/users.
- **Offline-Support**: Handled via Room in `:core:database`, with data modules managing synchronization.

## 🏗 Project Architecture

The project follows a 5-layer modular architecture with strictly unidirectional dependencies: **feature → domain (optional) → data → core**.

### 1. `:feature:*`
Each screen or logical flow is a separate Gradle module.
- **Location**: `feature/` directory.
- **Internal Structure**: 
  - `presentation/`: Compose UI and ViewModels.
  - `navigation/`: Navigation entries and route definitions.
  - `di/`: Hilt modules for feature-specific dependencies.
  - `domain/`: (Optional) UseCases or Interactors used only by this feature.

### 2. `:domain:*` (Optional)
Created only if logic is shared across features or requires a complex Interactor.
- **Location**: `domain/` directory.
- **Rule**: If a UseCase is used by only one feature, it stays in the feature's `domain/` package.

### 3. `:data:*`
Handles data retrieval from network or database.
- **Location**: `data/` directory.
- **Components**: Repository and DataSources (Remote/Local).
- **Rule**: No interfaces for Repositories unless strictly necessary.

### 4. `:core:*`
Shared infrastructure and utilities.
- **`:core:models`**: Pure domain models (no dependencies).
- **`:core:design`**: Design system and shared UI components.
- **`:core:network`**: Ktor client, TokenStorage, and API error handling.
- **`:core:database`**: Room database, entities, and DAOs.
- **`:core:navigation-api`**: Route constants and navigation contracts.

### 5. `:app`
The entry point that ties all modules together, implements navigation, and provides DI root.

---

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Dependency Injection**: Hilt
- **Network**: Ktor
- **Serialization**: Kotlin Serialization
- **Database**: Room
- **Concurrency**: Coroutines & Flow
- **Navigation**: Type-safe navigation based on `:core:navigation-api`

---

## 📋 Development Best Practices

### 1. UseCase & Interactor Selection
Follow this checklist from `README.md`:
- **Simple logic, one screen**: Directly in ViewModel.
- **Complex logic, one screen**: `domain/` package inside feature module.
- **Shared logic**: Dedicated `:domain:*` module.
- **Multiple UseCases scenario**: Interactor (inside feature or domain module).

### 2. Data Mapping
Always use **Mappers** to transform data between layers:
- `Request/Response DTO` (in `:data`) ↔ `Domain Model` (in `:core:models`).
- `Database Entity` (in `:core:database`) ↔ `Domain Model`.

### 3. Error Handling
Use `withApiException` (from `:core:network`) in repositories to handle Ktor exceptions and map them to `ApiException` types.

### 4. UI State Structure
Use a `sealed interface` for UI states. Always nest implementations inside:
```kotlin
sealed interface MyUiState {
    data object Loading : MyUiState
    data class Success(val data: MyData) : MyUiState
    data class Error(val message: String) : MyUiState
}
```

---

## 📂 Key File Locations

- **Domain Models**: `core/models/src/main/java/com/xando/core/models/`
- **Network Config**: `core/network/src/main/java/com/xando/core/network/`
- **Database/DAO**: `core/database/src/main/java/com/xando/core/database/`
- **Feature UI**: `feature/[name]/src/main/java/com/xando/[name]/presentation/`

---

## 🤖 Guide for AI Agents

When assisting with this project:
- **Persona Role**: Senior Developer. Prioritize modularity and Clean Architecture.
- **Modern Stack**: All new UI must be **Jetpack Compose**.
- **Theming**: Use `MaterialTheme.colorScheme`. Do not hardcode colors.
- **DI**:
    - Use `@Inject` for constructors in `:data`, `:feature`, and `:app`.
    - Register `:core` components in their respective Hilt modules.
- **Documentation**: Use **KDoc** for public APIs and complex logic.
- **File Organization**: One class per file, except for nested sealed classes/interfaces.
- **Dependencies**: Follow the strict direction: Feature → Domain → Data → Core. Never add `:app` as a dependency.