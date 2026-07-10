# `com.badlogic.gdx` — FFI

Thin Clojure mirror of the [libGDX](https://libgdx.com/) Java API. This tree is **foreign function interface only**: it exposes Java classes as Clojure vars. It is not the game API and not idiomatic Clojure.

Game code should not require this layer directly. Use `gdx.*` (Clojure API) or `clojure.*` (game domain) instead.

## Layer stack

```
clojure.moon.*          game — entities, txs, rules (no libGDX imports)
        ↑
gdx.*                   Clojure API — documented, idiomatic, short names
        ↑
com.badlogic.gdx.*      FFI (this tree) — mirror Java 1:1
        ↑
libGDX (Java)
```

| Layer | Job | Docs |
|-------|-----|------|
| `com.badlogic.gdx.*` | Curated mirror — only what this project uses | Javadoc copied 1:1 onto vars (pinned version) |
| `gdx.*` | Clojure-shaped API over FFI | Clojure docs — keywords, examples, ergonomics |
| `clojure.moon.*` | Game | Domain docs / specs |

## Rules

### 1. No logic

FFI vars only forward to Java. No `case`, `cond`, keyword mapping, defaults, validation, or game semantics.

If it needs a `case` or domain knowledge, it belongs in `gdx.*` or `clojure.*`.

**Allowed:** `proxy` / `reify` only where Java requires subclassing (e.g. anonymous `Actor`, `ApplicationListener`). Callback bodies are passed in from above; the proxy wiring stays here.

### 2. Names mirror Java (camelCase)

Clojure vars use the same names as Java methods and fields.

| Java | FFI |
|------|-----|
| `Input.getX()` | `getX` |
| `Input.isKeyPressed()` | `isKeyPressed` |
| `Application.postRunnable()` | `postRunnable` |
| `Input.Keys.D` | `D` (as `def`) |

Do not use kebab-case (`get-x`), `!` suffixes (`set-input-processor!`), or `?` suffixes (`visible?`) on FFI vars. Use Java names (`setInputProcessor`, `isVisible`).

### 3. Interop style

Use instance calls with a type hint on the receiver, not `Class/.method`:

```clj
;; yes
(defn getX [input]
  (.getX ^Input input))

;; no
(defn get-x [input]
  (Input/.getX input))
```

### 4. Constructors first

When a namespace has `defn new`, it must be the **first** `defn` in the file (after `ns` / imports).

```clj
(defn new []
  (TiledMap.))

(defn getLayers [tiled-map]
  (.getLayers ^TiledMap tiled-map))
```

### 5. Static fields → `def`

Constants and enum-like static fields are `def`, not functions.

```clj
(ns com.badlogic.gdx.input$keys
  (:import (com.badlogic.gdx Input$Keys)))

(def D Input$Keys/D)
(def SPACE Input$Keys/SPACE)
```

Keyword → int conversion (e.g. `:input.keys/d` → `D`) lives in `clojure.input$keys`, not here.

### 6. One namespace per Java class

Namespace path follows the Java package and class name:

```
com.badlogic.gdx.Input              → com.badlogic.gdx.input
com.badlogic.gdx.Input.Keys         → com.badlogic.gdx.input$keys
com.badlogic.gdx.scenes.scene2d.Actor → com.badlogic.gdx.scenes.scene2d.actor
```

Inner classes use `$` in the namespace name (Clojure convention).

### 7. Type hints

Put `^Type` on the receiver (first argument) for instance methods. Add hints on other parameters when interop needs them (e.g. `^InputProcessor`, `^String`).

Field access on Java objects may use hints on the local binding: `(set! (.x ^Vector3 v) x)`.

### 8. Exclude clashing core names

Use `(:refer-clojure :exclude [...])` when FFI names collide with `clojure.core` (`new`, `get`, `put`, `remove`, `load`, etc.).

## Documentation

We pin libGDX (see `project.clj`). FFI names mirror Java 1:1 — **docstrings copy Java javadoc 1:1** from that same pinned version.

Because this layer is a **curated abstraction** (only exposed members exist), every public var gets a docstring. `(doc …)` in the REPL should be enough; no browser required for day-to-day work.

### Pinned sources

| Artifact | Version / ref |
|----------|----------------|
| `com.github.damn/com.badlogic.gdx` | git SHA in `project.clj` |
| `gdx-backend-lwjgl3`, natives, freetype | `1.14.2` |

When bumping libGDX, refresh docstrings for touched namespaces from javadoc for that version.

### This file

Layer-wide rules and architecture. Read this once.

### Per-namespace docstring

Class-level javadoc (first paragraph) plus link to the class page:

```clj
(ns com.badlogic.gdx.input
  "Input processor and polling. FFI for com.badlogic.gdx.Input.
   https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/Input.html"
  (:import (com.badlogic.gdx Input)))
```

Use the javadoc URL that matches the pinned release, not necessarily nightlies, when a stable doc set exists.

### Per-var docstrings — copy Java 1:1

Copy the method/field javadoc from the pinned libGDX sources into the Clojure docstring. Keep HTML out; plain text or minimal markdown is fine.

Names already match (`getX` ↔ `getX()`), so docs match too:

```clj
(defn getX
  "Returns the x coordinate of the last touch on touch screen devices and the current mouse position on desktop for mouse
  and touchpad input. The screen is included in the input area and has the same origin (0,0) as the screen.
  This means that the x coordinate returned will be larger than the screen width if the touch was made right of the screen."
  [input]
  (.getX ^Input input))
```

For `def` static fields, copy the field javadoc the same way.

**Do not** rewrite or summarize in FFI — copy from the pinned javadoc, then only fix formatting for Clojure strings (escape quotes, strip `{@link Foo}` to plain `Foo` if needed).

### What `gdx.*` documents instead

| FFI doc | `gdx` doc |
|---------|-----------|
| Java javadoc, copied 1:1 | Clojure API — keyword opts, `[x y]`, when to clojurize |
| What `Input#getX` does | What `get-x` means in your app, defaults, composition |

FFI = **what libGDX says**. `gdx` = **how we use it in Clojure**.

### Adding a new binding

1. Add the FFI var (name mirrors Java).
2. Copy javadoc from pinned libGDX for that method/field.
3. Wrap in `gdx.*` only if game code needs a Clojure-shaped surface.

Optional later: a small tool that reads javadoc from the pinned JAR and emits docstrings when scaffolding a new namespace — same rules as manual copy.

### REPL fallback

If a docstring is missing or stale, look up the Java member directly:

```clj
(require '[clojure.java.javadoc :refer [javadoc]])
(javadoc com.badlogic.gdx.Input/getX)
```

Or [clojure.java.doc](https://github.com/clojure/java.javadoc) / CIDER `cider-javadoc`.

## What does not belong here

| Concern | Where |
|---------|--------|
| `:input.keys/d` → key code | `clojure.input$keys` |
| `clojurize` Vector2 → `[x y]` | `gdx.vec2` / `clojure.vector2` |
| App bootstrap, keyword config maps | `gdx.app` |
| Inventory UI, game txs | `clojure.moon.*` |
| Pure `[x y]` math | `clojure.v2.*` |

## File template

```clj
(ns com.badlogic.gdx.example
  "Class-level javadoc (first paragraph). FFI for com.badlogic.gdx.Example.
   https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/Example.html"
  (:refer-clojure :exclude [new]) ; if needed
  (:import (com.badlogic.gdx Example)))

(defn new []
  (Example.))

(defn someMethod
  "Method javadoc copied 1:1 from pinned libGDX Example#someMethod."
  [example arg]
  (.someMethod ^Example example arg))
```

## Migration

This tree is being migrated to the rules above (camelCase names, `.method ^Type` interop, constructors at top, static fields as `def`). Files not yet updated still follow the old style (`get-x`, `Class/.method`) until touched.

When editing a file, bring it fully in line with this README.
