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
| `com.badlogic.gdx.*` | Mirror methods, constructors, static fields | This file + one javadoc link per namespace |
| `gdx.*` | Clojure-shaped API over FFI | Docstrings on public vars |
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

### This file

Layer-wide rules and architecture. Read this once.

### Per-namespace docstring

Each `.clj` file has **one** namespace docstring with a link to the mirrored Java class javadoc. No per-var docstrings — var names already match Java.

```clj
(ns com.badlogic.gdx.input
  "FFI for com.badlogic.gdx.Input.
   https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/Input.html"
  (:import (com.badlogic.gdx Input)))
```

Pin the javadoc base URL in one place when we add `com.badlogic.gdx.javadoc` (optional shared helper).

### Behavior details

For what a method *does*, use Java javadoc — not duplicated prose here.

In the REPL:

```clj
(require '[clojure.java.javadoc :refer [javadoc]])
(javadoc com.badlogic.gdx.Input)
```

Or use [clojure.java.doc](https://github.com/clojure/java.javadoc) / CIDER `cider-javadoc` for in-editor lookup.

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
  "FFI for com.badlogic.gdx.Example.
   https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/Example.html"
  (:refer-clojure :exclude [new]) ; if needed
  (:import (com.badlogic.gdx Example)))

(defn new []
  (Example.))

(defn someMethod [example arg]
  (.someMethod ^Example example arg))
```

## Migration

This tree is being migrated to the rules above (camelCase names, `.method ^Type` interop, constructors at top, static fields as `def`). Files not yet updated still follow the old style (`get-x`, `Class/.method`) until touched.

When editing a file, bring it fully in line with this README.
