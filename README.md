# LumosTest — Android Take-Home Assignment

A production-quality Android app built with Jetpack Compose, Clean Architecture, and MVVM.
Displays a paginated product catalog from the DummyJSON API with full offline support.

---

## Setup

1. Clone the repository
2. Open in Android Studio Meerkat (2024.3) or later
3. Wait for Gradle sync to complete
4. Run on an emulator or physical device (minSdk 24)

No API keys or secrets required — DummyJSON is a free, public API.

---

## Architecture

```
┌─────────────────────────────────────────────────┐
│                  UI Layer                        │
│  ProductListScreen  ←→  ProductListViewModel     │
│  ProductDetailScreen ←→ ProductDetailViewModel  │
│  (Jetpack Compose + StateFlow)                   │
└───────────────────┬─────────────────────────────┘
                    │ domain model + UiState
┌───────────────────▼─────────────────────────────┐
│               Domain Layer                       │
│  ProductRepository (interface)                   │
│  Product (data class, no Android deps)           │
│  DataResult<T> (Success/Error + isFromCache)     │
└───────────────────┬─────────────────────────────┘
                    │ implemented by
┌───────────────────▼─────────────────────────────┐
│                Data Layer                        │
│  ProductRepositoryImpl                           │
│  ├── ProductApi (Retrofit)   → DummyJSON REST    │
│  └── ProductDao (Room)       → local SQLite DB   │
└─────────────────────────────────────────────────┘
```

### Dependency Injection
Hilt wires all layers together via three modules:
- `NetworkModule` — OkHttp + Retrofit + ProductApi
- `DatabaseModule` — Room database + DAO
- `RepositoryModule` — binds interface to implementation

---

## Key Decisions

| Decision | Choice | Reason |
|---|---|---|
| Pagination | Manual offset (skip/limit) | Transparent, simple, no Paging3 complexity |
| Cache strategy | Room offline-first | Network → save to Room → on failure serve Room |
| Pull-to-refresh | Material3 `PullToRefreshBox` | First-party, no extra dependency |
| Result wrapper | Custom `DataResult<T>` | Carries `isFromCache` metadata that `kotlin.Result` cannot |
| Image loading | Coil | Compose-native, coroutine-backed |
| No use-case layer | Omitted | Two-screen app; repository is thin enough |

---

## States Handled

| Scenario | Behaviour |
|---|---|
| First load | Full-screen spinner |
| Data loaded | Animated list (fade + slide in) |
| Pagination | Spinner footer, items appended |
| Pull-to-refresh | Refreshes from network, clears cache page |
| Offline (cache hit) | Shows data + amber "cached data" banner |
| Offline (no cache) | Full-screen error + Retry button |
| Empty API response | Full-screen empty state |

---

## Running Tests

```bash
./gradlew test
```

Unit tests cover `ProductRepositoryImpl` with MockK:
- Network success path (data saved to Room, `isFromCache = false`)
- Network error + cache hit (`isFromCache = true`)
- Network error + empty cache (returns `DataResult.Error`)

---

## Project Structure

```
app/src/main/java/app/naman/lumostest/
├── data/
│   ├── api/           Retrofit interface + DTOs
│   ├── db/            Room database, DAO, entity
│   └── repository/    ProductRepositoryImpl
├── domain/
│   ├── model/         Product, DataResult
│   └── repository/    ProductRepository interface
├── di/                Hilt modules
├── ui/
│   ├── list/          ProductListScreen + ViewModel
│   ├── detail/        ProductDetailScreen + ViewModel
│   ├── navigation/    NavHost/NavGraph
│   └── theme/         Material3 theme (unchanged)
├── LumosTestApp.kt    @HiltAndroidApp
└── MainActivity.kt    @AndroidEntryPoint
```

---

## Limitations / Known Trade-offs

- Images stored as comma-separated string in Room (simpler than TypeConverter for this scope)
- No search or filtering
- No dark-mode specific image handling
- Pull-to-refresh only resets page 1; subsequent pages are re-fetched lazily on scroll
