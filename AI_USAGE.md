# AI Usage Disclosure

## Tools Used
- Claude (Claude Code CLI — claude-sonnet-4-6)

## Usage Log

### Step 1 — Dependencies
- **Files affected:** `gradle/libs.versions.toml`, `app/build.gradle.kts`, `build.gradle.kts`
- **What AI generated:** Full dependency catalog with Hilt/KSP, Retrofit, Room, Navigation Compose, Coil, MockK versions
- **What was modified:** KSP version was verified manually against Kotlin 2.0.21 compatibility matrix before accepting
- **What was rejected:** AI initially suggested using Paging3; rejected in favour of manual offset pagination for transparency and simplicity
- **Judgment call:** Chose `composeBom = "2025.05.00"` over the original `2024.09.00` to get stable Material3 `PullToRefreshBox` API

### Step 2 — Domain Layer
- **Files affected:** `domain/model/Product.kt`, `domain/repository/ProductRepository.kt`
- **What AI generated:** Pure Kotlin domain model and repository interface using `Flow<Result<...>>`
- **What was modified:** Added `isFromCache` flag to a custom `DataResult` wrapper instead of using stdlib `Result` (which can't carry metadata)
- **What was rejected:** AI suggestion to use `use-cases` layer — omitted as overkill for a two-screen app

### Step 3 — Data Layer
- **Files affected:** All files under `data/api/`, `data/db/`, `data/repository/`
- **What AI generated:** DTO classes, Retrofit interface, Room entity/DAO/database, `ProductRepositoryImpl`
- **What was modified:** Repository offline strategy refined: Room data is returned with `isFromCache=true` banner instead of silently replacing network data
- **What was rejected:** AI suggested RxJava flows; kept coroutines/Flow throughout

### Step 4 — DI Modules
- **Files affected:** `di/NetworkModule.kt`, `di/DatabaseModule.kt`, `di/RepositoryModule.kt`
- **What AI generated:** Hilt `@Module` / `@Provides` / `@Binds` boilerplate
- **What was modified:** Added `OkHttpClient` with a `HttpLoggingInterceptor` for debug builds only (not in AI output)
- **What was rejected:** None

### Step 5 — UI State + List Feature
- **Files affected:** `ui/list/ProductListViewModel.kt`, `ui/list/ProductListScreen.kt`
- **What AI generated:** `UiState` sealed class, ViewModel with pagination state, `LazyColumn` with scroll detection
- **What was modified:** Replaced AI-suggested `SwipeRefresh` (third-party) with Material3 `PullToRefreshBox` (first-party)
- **What was rejected:** AI added a `rememberCoroutineScope` inside the list item for animation; moved animation to a shared `AnimatedVisibility` wrapper instead

### Step 6 — Detail Feature
- **Files affected:** `ui/detail/ProductDetailViewModel.kt`, `ui/detail/ProductDetailScreen.kt`
- **What AI generated:** Detail ViewModel, hero image layout, product metadata fields
- **What was modified:** Added image pager (horizontal scroll through `images` list) not in original AI output
- **What was rejected:** None

### Step 7 — Navigation + MainActivity
- **Files affected:** `ui/navigation/AppNavGraph.kt`, `MainActivity.kt`, `LumosTestApp.kt`, `AndroidManifest.xml`
- **What AI generated:** `NavHost` setup, `@AndroidEntryPoint`, `@HiltAndroidApp`
- **What was modified:** Nothing significant
- **What was rejected:** None

### Step 8 — Unit Tests
- **Files affected:** `test/.../ProductRepositoryImplTest.kt`
- **What AI generated:** MockK-based tests for network success, network error (cache fallback), and empty cache paths
- **What was modified:** Test structure reorganised for clarity
- **What was rejected:** None

### Step 9 — Image Cache Strategy (Naman-Identified Gap)
- **Reported by:** Naman — *"The cached data don't cache the images"*
- **Root cause:** The original AI-generated plan cached only image URLs in Room. It never configured Coil's `ImageLoader`, so Coil used its default in-memory-only cache. Images were lost on process kill or offline restart.
- **Files affected:** `LumosTestApp.kt`
- **What AI generated (initial, insufficient):** `LumosTestApp` was a bare `@HiltAndroidApp` Application with no `ImageLoader` configuration.
- **Naman's improvement:** Identified that image bytes — not just URLs — must be persisted to survive offline. Prompted the two-tier caching strategy below.
- **What was implemented:** `LumosTestApp` now implements `ImageLoaderFactory` with:
  1. **Coil `DiskCache`** (100 MB) — stores decoded/transformed bitmaps in `cacheDir/image_cache`
  2. **OkHttp `Cache`** (50 MB HTTP cache) — stores raw compressed image bytes in `cacheDir/image_http_cache`
  3. **`addNetworkInterceptor`** — overrides restrictive `Cache-Control` headers from the DummyJSON CDN (which may send `no-cache`/`no-store`) and forces `max-age=604800` (7 days) on every image response
  4. **`MemoryCache`** — in-process LRU cache at 25% of app memory for instant scrolling
- **Judgment call:** The CDN header override (step 3) was the critical insight — without it, OkHttp would obey `no-cache` from the server and the HTTP-level cache would never persist. The network interceptor runs post-response and rewrites headers before OkHttp stores the response, making offline images reliable regardless of what the CDN sends.

### Step 10 — TopAppBar Connectivity Status Icon
- **Files affected:** `ui/list/ProductListScreen.kt`, `app/build.gradle.kts`, `gradle/libs.versions.toml`
- **What AI generated:** Moved offline indicator from a text banner (`Surface + Text` strip below TopAppBar) into an icon in the TopAppBar `actions` slot. `AnimatedContent` crossfades between `WifiOff` (amber `#FFB300`) when serving cached data and `Wifi` (white) when live data is loaded. Icon is absent during Loading/Error/Empty states. Added `material-icons-extended` dependency (`Wifi`/`WifiOff` are not in the core icon set).
- **What was modified:** Nothing beyond what was specified in the plan
- **What was rejected:** None

### Step 11 — Shimmer Skeleton Loading + Remove List Entrance Animation
- **Files affected:** `ui/list/ProductListScreen.kt`
- **What AI generated:** Removed per-item `AnimatedVisibility` / `fadeIn` / `slideInVertically` entrance animation from `LazyColumn` items. Replaced `LoadingContent` (single centered `CircularProgressIndicator`) with 6 shimmer skeleton cards in a `LazyColumn`. `ShimmerProductCard` mirrors the real card layout (80 dp image box + 3 text placeholder bars) with a sweeping `LinearGradient` driven by `InfiniteTransition` / `LinearEasing`. Colors use `MaterialTheme.colorScheme.surfaceVariant` so the shimmer is theme-aware (light + dark).
- **What was modified:** Nothing beyond what was requested
- **What was rejected:** None

### Step 12 — Quality Hardening Pass (Codex Contribution)
- **Files affected:** `domain/model/AppError.kt`, `domain/model/DataResult.kt`, `ui/UiState.kt`, `data/repository/ProductRepositoryImpl.kt`, `data/db/ProductDao.kt`, `ui/list/ProductListViewModel.kt`, `ui/detail/ProductDetailViewModel.kt`, `ui/list/ProductListScreen.kt`, `ui/detail/ProductDetailScreen.kt`, `di/DatabaseModule.kt`, `app/build.gradle.kts`, `gradle/libs.versions.toml`, `test/.../ProductRepositoryImplTest.kt`, `.github/workflows/android-ci.yml`, `.gitignore`, `AI_USAGE.md`
- **What AI generated:** Typed error model (`AppError`) across domain/data/UI; repository cache-first emission flow (emit cached page/detail/category first when present, then refresh from network), category cache invalidation on refresh (`deleteByCategory`), duplicate-safe pagination append logic, lifecycle-aware state collection in Compose (`collectAsStateWithLifecycle`), Room migration path (v1→v2 categories table) replacing destructive migration fallback, CI workflow (lint + unit tests + assemble), and expanded repository unit tests for cache-first and error typing.
- **What was modified:** Fixed pre-existing compile blocker in detail screen by correctly hoisting full-screen image viewer state (`fullScreenIndex`) through `ProductDetail(...)` parameters. Updated detail image navigation icons to `AutoMirrored` variants to remove deprecation warnings.
- **What was rejected:** Full project modularization into `core/feature` modules was intentionally skipped for this pass per Naman instruction.
- **Validation performed:**
  1. `./gradlew testDebugUnitTest --no-daemon` (pass)
  2. `./gradlew lintDebug assembleDebug --no-daemon` (pass)
  3. Removed ExFAT AppleDouble metadata files (`._*`) from project tree and added ignore rule to prevent recurrence.

## Naman Product & UI Direction Ownership

Naman defined the complete UI layout and feature direction for this app. Implementation assistance (Claude) was used to convert Naman's product and design decisions into code.

Naman-owned ideas and guided decisions include:
1. Splash screen concept and style direction
2. Theme system with multiple palette options
3. Dark mode and light mode behavior
4. Category navigation rail pattern
5. Product-detail image viewer experience, including zoom and pan on each image item
6. Overall feature set, UI flow, and interaction behavior across list/detail screens

## Summary

AI assistance was used for all boilerplate, architecture scaffolding, and standard patterns (Hilt modules, Room DAOs, Retrofit interface). Human judgment was applied for:
1. Choosing manual pagination over Paging3 for simplicity
2. Selecting first-party Material3 pull-to-refresh over third-party library
3. Adding `isFromCache` metadata to the Result wrapper (AI used plain `Result<T>`)
4. Adding HTTP logging interceptor for debug builds
5. Skipping the use-case layer as unnecessary for this scope
6. **Identifying the missing image cache layer** — AI cached URLs in Room but not image bytes; Naman caught that offline images were blank and directed the two-tier Coil + OkHttp disk cache fix
7. **UI polish direction** — Naman directed moving the offline status from a text banner into a TopAppBar icon, and replacing spinner + list entrance animations with a shimmer skeleton loader
8. **Production hardening choices** — moved from string-matching errors to typed error handling, removed destructive DB migration fallback in favour of explicit migration, added CI checks, and validated end-to-end with lint + unit tests + debug assembly
