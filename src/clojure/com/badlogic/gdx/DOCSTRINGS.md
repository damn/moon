# Facade docstring generation

How the Clojure docstrings under `src/clojure/com/badlogic/gdx/` were produced from LibGDX JavaDocs.

## Goal

- Copy Java documentation onto the Clojure facades (ns + var docs).
- Adjust text so each facade is standalone: no raw Java FQCNs / `{@link}`, only what the facade exposes.
- Use Codox markdown cross-links (`:doc/format :markdown` in `project.clj`) when a referenced type/member has a facade counterpart.

## Sources

- LibGDX **1.14.2** (same version as `project.clj` deps).
- Maven sources jars:
  - `gdx-1.14.2-sources.jar`
  - `gdx-backend-lwjgl3-1.14.2-sources.jar`
  - `gdx-freetype-1.14.2-sources.jar`
- Extracted under `/tmp/gdx-sources` during generation.

## Process (high level)

1. **Catalog facades**  
   Walk every `*.clj` under this tree. For each `ns` / `defn` / `def`, record:
   - Clojure ns and var name
   - Primary Java type (from filename heuristics + `:import`)
   - Interop target: instance call (`.play`), static call (`Colors/put`), field (`Gdx/audio`, `set!` / `.zoom`), or constructor (`Texture.`)

2. **Extract JavaDocs**  
   For each mapped member, find the javadoc immediately preceding the matching declaration in the Java sources (including parent types when needed, e.g. `OrthographicCamera` → `Camera.update`).  
   Skip Apache license banner comments.  
   Only emit a Clojure docstring when Java actually had one — do not invent docs.

3. **Transform text**
   - Strip HTML; turn `@return` into `Returns …`.
   - Keep `@param` only when a matching Clojure parameter exists (by normalized name); drop params the facade does not take (e.g. `play!` uses no-arg `play()`, not volume overloads).
   - Resolve `{@link …}`:
     - Facade var → `[var]` (same ns) or `[ns/var]`
     - Facade ns → `[[ns]]`
     - No facade → plain English (e.g. `Application` → “application”)
   - Collapse duplicate links from overloaded Java refs (`[play!] or [play!]` → `[play!]`).
   - Soften leftover Java type names where useful (`ApplicationListener` → “application listener”).

4. **Write docs**
   - **Class/interface** JavaDoc → `ns` docstring.
   - **Method / field / constructor** JavaDoc → `defn` / `def` docstring (constructor docs on `create` when present; class overview stays on the ns).
   - Clojure-only helpers (`reify` / `proxy` wiring, `instance?` predicates) stay without a var docstring.

5. **Spot-check & polish**  
   Manually fix a few awkward transforms (e.g. viewport `update!` / `unproject!`, colors `put`, gdx “Do not use Graphics”, sound wording).  
   Verify all facade namespaces load.

## Mapping rules of thumb

| Clojure form | Java source |
| --- | --- |
| `(ns …)` | Class/interface javadoc of the primary type |
| `(defn play! [sound] (.play sound))` | Matching arity method (`play()`), not other overloads |
| `(defn create […] (Foo. …))` | Constructor javadoc for that arity (if any) |
| `(def white Color/WHITE)` | Field javadoc (if any) |
| `(defn dispose! [d] (.dispose d))` | `Disposable.dispose` (and similar) |

Primary type ↔ ns is keyed off the facade’s main type only. **Imports are not** registered as that ns — otherwise e.g. `Camera` imported by fit-viewport would steal `{@link Camera}` links.

## Codox links

Examples after transform:

- Same-ns var: `[play!]`, `[begin]`
- Other-ns var: `[clojure.com.badlogic.gdx.audio/new-sound]`
- Namespace: `[[clojure.com.badlogic.gdx.audio.sound]]`
- Shared dispose: `[clojure.com.badlogic.gdx.utils.disposable/dispose!]`

## What was left undocumented

- Java members with **no** javadoc (many getters, vertex index constants, key/button constants, etc.).
- Types with **no** class javadoc (e.g. `Lwjgl3Application`).
- Pure Clojure helpers with no Java counterpart.

Rough counts from the generation pass: **~70** ns docs, **~149** var docs across **73** facade files.

## Regenerating

There is no checked-in generator script in the repo; the one-off tooling lived under `/tmp` during the session (`add_facade_docs.py` + extracted sources). To regenerate later:

1. Download/extract the three 1.14.2 sources jars.
2. Re-run a catalog → extract → transform → write pass with the rules above.
3. Re-spot-check Codox links and any docs that mention types without facades.
4. `require` all `clojure.com.badlogic.gdx.*` namespaces to confirm syntax.

Prefer editing individual docstrings by hand for small fixes; full regen is for bulk upgrades (e.g. LibGDX version bump).
