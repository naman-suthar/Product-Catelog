# App Features

## Core Experience
1. Animated splash screen with branded intro.
2. Product list screen with shimmer skeleton loading.
3. Pull-to-refresh on product list.
4. Infinite scroll pagination (offset-based).
5. Offline fallback from local cache when network fails.
6. Offline status indicator in main screen TopAppBar.

## Browsing & Discovery
1. Category navigation rail with `All` + category filtering.
2. Product cards with thumbnail, title, brand, price, and rating.
3. Empty-state UI when no products are available.
4. Rich error-state UI with retry action.

## Product Detail
1. Detail screen with hero image carousel.
2. Thumbnail strip synced with hero pager.
3. Full-screen image viewer for product images.
4. Zoom and pan for each image item.
5. Arrow-based image navigation in full-screen mode.
6. Image counter in full-screen mode.

## Theming & Personalization
1. Multiple theme color palette options.
2. Dark mode / light mode toggle.
3. Persisted theme preferences using `SharedPreferences`.

## Data, Architecture & Reliability
1. Retrofit networking with DummyJSON API integration.
2. Room local database caching for products and categories.
3. Hilt dependency injection across app layers.
4. Coil image loading with disk + memory caching.
5. OkHttp HTTP image cache with cache-control override for offline image reliability.
6. Typed error handling (`network`, `timeout`, `http`, `unknown`) across data/UI flow.
7. Repository cache-first behavior with network refresh.
8. Category refresh cache invalidation to avoid stale category lists.
9. Explicit Room migration path (non-destructive upgrade flow).

## Quality & Delivery
1. Unit tests for repository success, fallback, and error scenarios.
2. CI workflow for `lintDebug`, `testDebugUnitTest`, and `assembleDebug`.

