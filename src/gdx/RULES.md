# GDX — libGDX in Clojure form

`gdx.*` wraps [libGDX](https://libgdx.com/). It is not game code and not a generic platform layer — it is **libGDX math, graphics, and utils** with Clojure-friendly names and boundaries.

libGDX already handles LWJGL3 / Android / iOS backends. `gdx.*` does not.

## Layer stack

```
moon.*              game — rules, shapes, [x y] math (moon.v2)
        ↑
gdx.*               libGDX types & ops — clojurize, keyword opts, pipelines
        ↑
com.badlogic.gdx.*  FFI — Java mirror only (no logic)
        ↑
libGDX (Java)
```

## Requires

| Layer | May require |
|-------|-------------|
| `com.*` | Java / other `com.*` for the same binding |
| `gdx.*` | `com.*` for **its** Java type only + other `gdx.*` |
| `moon.*` | `gdx.*` and real Clojure — **never** `com.*` |

Example: a gdx wrapper for viewport would require `com.badlogic.gdx.utils.viewport` and `gdx.math.vector2` — not `com.badlogic.gdx.math.vector2` directly.

## FFI (`com.*`) rules

- Mirror Java 1:1 (camelCase names).
- Forward only — no `case`, no `clojurize`, no keyword opts.
- See [ffi-guideline.md](../docs/ffi-guideline.md) in repo docs.

## GDX (`gdx.*`) rules

- One Java class → one `gdx.*` namespace (drop `com.badlogic.` prefix, keep rest: `gdx.math.vector2`).
- Add adaptation here: `new`, `clojurize`, keyword opts, listener maps.
- Pipelines (e.g. draw tiled map) live under the **subject** namespace (`gdx.maps.tiled.draw`), not under unrelated Java packages.

## Math vs game vectors

| | Type | Namespace |
|--|------|-----------|
| libGDX mutable Vector2/3 | Java object | `gdx.math.vector2`, `gdx.math.vector3` |
| Pure `[x y]` game math | Clojure vector | `moon.v2` |

Convert at the boundary with `gdx.math.*/clojurize`. Game code stays on `[x y]`.

## Adding a new binding

1. Add FFI var in `com.*` if missing.
2. Add `gdx.*` wrapper only when moon or another gdx ns needs a Clojure face.
3. Moon requires `gdx.*`, not `com.*`.
