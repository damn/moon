# FFI Guideline

Rules for `com.badlogic.gdx.*` — the thin Clojure mirror of [libGDX](https://libgdx.com/).

This layer is **foreign function interface only**. It exposes Java classes as Clojure vars. It is not the game API and not idiomatic Clojure.

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
| `com.badlogic.gdx.*` | Curated mirror — only what this project uses | None — names mirror Java; use libGDX javadoc when needed |
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

Do **not** use kebab-case (`get-x`), `!` suffixes (`set-input-processor!`), or `?` suffixes (`visible?`) on FFI vars. Use Java names (`setInputProcessor`, `isVisible`).

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
com.badlogic.gdx.Input                    → com.badlogic.gdx.input
com.badlogic.gdx.Input.Keys               → com.badlogic.gdx.input$keys
com.badlogic.gdx.scenes.scene2d.Actor     → com.badlogic.gdx.scenes.scene2d.actor
```

Inner classes use `$` in the namespace name (Clojure convention).

One class, one namespace. No cross-dependencies between FFI namespaces.

### 7. Type hints

Put `^Type` on the receiver (first argument) for instance methods. Add hints on other parameters when interop needs them (e.g. `^InputProcessor`, `^String`).

Field access on Java objects may use hints on the local binding:

```clj
(set! (.x ^Vector3 v) x)
```

### 8. Exclude clashing core names

Use `(:refer-clojure :exclude [...])` when FFI names collide with `clojure.core` (`new`, `get`, `put`, `remove`, `load`, etc.).

### 9. No docstrings

FFI namespaces and vars have **no docstrings**. Names mirror Java 1:1 — the binding is self-explanatory. For behavior details, read libGDX javadoc for the pinned version (see `project.clj`).

Documentation belongs in `gdx.*` (Clojure API) and `clojure.moon.*` (game domain), not here.

## What does not belong here

| Concern | Where |
|---------|--------|
| Docstrings, examples, keyword opts | `gdx.*` |
| `:input.keys/d` → key code | `clojure.input$keys` |
| `clojurize` Vector2 → `[x y]` | `gdx.vec2` / `clojure.vector2` |
| App bootstrap, keyword config maps | `gdx.app` |
| Inventory UI, game txs | `clojure.moon.*` |
| Pure `[x y]` math | `clojure.v2.*` |

## File template

```clj
(ns com.badlogic.gdx.example
  (:refer-clojure :exclude [new]) ; if needed
  (:import (com.badlogic.gdx Example)))

(defn new []
  (Example.))

(defn someMethod [example arg]
  (.someMethod ^Example example arg))
```

## Adding a new binding

1. Add the FFI var (name mirrors Java).
2. Wrap in `gdx.*` only if game code needs a Clojure-shaped surface.

## Migration

This tree is being migrated to the rules above:

- camelCase names
- `.method ^Type` interop
- constructors at top
- static fields as `def`
- no docstrings

Files not yet updated still follow the old style (`get-x`, `Class/.method`) until touched.

**When editing a file, bring it fully in line with this guideline.**

## Quick reference

| Old style | New style |
|-----------|-----------|
| `get-x` | `getX` |
| `visible?` | `isVisible` |
| `add-actor!` | `addActor` |
| `Input/.getX input` | `(.getX ^Input input)` |
| `(defn get-x ...)` before `(defn new ...)` | `(defn new ...)` first |
| docstring on every var | no docstrings |
