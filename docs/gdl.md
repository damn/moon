GDL — Clojure Game development libraries
========================================

GDL is the Clojure-shaped API layer in `src/gdl/` (`gdl.*` namespaces).

It sits between game code (`clojure.*`) and the FFI mirror (`com.badlogic.gdx.*`):

```
clojure.*     game / domain
gdl.*         GDL
com.badlogic.gdx.*   FFI
libGDX (Java)
```

Rules:
- New libGDX wrappers go in `gdl/` only — folder path mirrors `com.badlogic.gdx.*` under `gdl/` (e.g. `gdl/scenes/scene2d/stage.clj` → `gdl.scenes.scene2d.stage`).
- Top-level libGDX entry points stay at `gdl/<pkg>.clj` (`gdl.application`, `gdl.files`, `gdl.graphics`, `gdl.input`, `gdl.audio`).
- Game code requires `gdl.*`, not `com.badlogic.gdx.*`.
- Domain logic (input helpers, camera math, UI composition) stays in `clojure.*`.
