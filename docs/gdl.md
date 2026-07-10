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
- New libGDX wrappers go in `gdl/` only — flat `gdl.*` names, not nested paths.
- Game code requires `gdl.*`, not `com.badlogic.gdx.*`.
- Domain logic (input helpers, camera math, UI composition) stays in `clojure.*`.
