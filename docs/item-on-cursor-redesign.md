# Item on cursor — simpler design proposal

Status: **proposal only** (not implemented).  
Current behaviour: [item-on-cursor.md](item-on-cursor.md)  
Manual QA: [item-on-cursor-manual-qa.md](item-on-cursor-manual-qa.md)

## Problem with current design

Ground drop is a **side effect of leaving FSM state** (`k-state-exit.player-item-on-cursor`), while inventory placement **also** leaves that state. Two exit paths, one exit handler:

```
pickup  → FSM :player-item-on-cursor  →  :entity/item-on-cursor on entity
place   → dissoc cursor FIRST         →  :dropped-item event              →  state-exit (must be noop)
drop    → :drop-item event             →  state-exit SPAWNS on ground
```

Inventory handlers must `dissoc` before `:dropped-item` so state-exit does not spawn a duplicate on the ground. Swap re-enters cursor state via `:pickup-item` after `:dropped-item`. Two events (`:drop-item`, `:dropped-item`) mean the same idle transition.

---

## Simpler model: dragging is data, not FSM state

Do **not** use the player FSM for inventory drag.

```clojure
:entity/dragging item   ; or nil
```

Pause game, hand cursor, and render ghost key off `(some? (:entity/dragging entity))` — same player-visible behaviour, no state enter/exit.

| User action | Explicit op |
|-------------|----------------|
| Click item in cell (not dragging) | `[:tx/inv/take cell]` |
| Click empty valid cell (dragging) | `[:tx/inv/place cell]` |
| Click occupied cell (dragging) | `[:tx/inv/swap cell]` or `[:tx/inv/stack cell]` |
| Click world, not UI (dragging) | `[:tx/inv/drop world-pos]` |
| (optional) Esc | `[:tx/inv/cancel]` — put back in source cell or drop |

Each handler does **everything** for that action in one place:

```clojure
;; place — no FSM, no exit side effects
[:tx/sound "bfxr_itemput"]
[:tx/set-item eid cell item]
[:tx/assoc eid :entity/dragging nil]

;; drop on ground
[:tx/sound "bfxr_itemputground"]
[:tx/spawn-item pos item]
[:tx/assoc eid :entity/dragging nil]
```

No `:dropped-item` vs `:drop-item`. No state-enter. **Empty state-exit** (or remove cursor state entirely).

---

## One function option

Collapse handlers into a single pure decision (easier to test):

```clojure
(inventory-op entity target frame) → txs
```

```clojure
(cond
  (and (nil? dragging) item-in-cell)   → take-txs
  (and dragging (empty? cell))        → place-txs
  (and dragging (stackable? ...))      → stack-txs
  (and dragging item-in-cell)          → swap-txs
  (and dragging world-click)           → drop-txs
  :else nil)
```

Inventory cell clicks and world clicks both call this. Manual QA checklist maps 1:1 to `deftest` cases.

UI sync can stay separate later:

```clojure
[:tx/set-item ...]           ; sim only
[:tx/ui/sync-inventory ...]  ; projection from sim (optional second step)
```

---

## Swap without FSM round-trip

Today: place → `:dropped-item` → `:pickup-item` → enter cursor state again.

Simpler:

```clojure
[:tx/set-item eid cell dragging-item]
[:tx/assoc eid :entity/dragging item-from-cell]
```

Still dragging; different item. No exit, no re-enter.

---

## What to delete (after migration)

| Remove | Why |
|--------|-----|
| `:player-item-on-cursor` FSM state | `:entity/dragging` replaces it |
| `k-state-enter` / `k-state-exit` for cursor | No side-effect exit |
| `:drop-item` / `:dropped-item` events | Explicit `:tx/inv/drop` / `:tx/inv/place` |
| `dissoc` before event hack | Exit no longer spawns on ground |
| Duplicate `:entity/item-on-cursor` + FSM state component | One field |

**Keep:** `item-place-position`, `valid-slot?`, `stackable?`, sounds, `spawn-item`, `set-item`.

---

## Migration order (small steps)

1. Add `:entity/dragging` parallel to `:entity/item-on-cursor`; wire render / cursor / pause to either (no behaviour change).
2. Implement `:tx/inv/*` handlers; leave old path in place or make state-exit a noop.
3. Switch inventory + world-click handlers to `:tx/inv/*`; remove FSM cursor state when tests + [manual QA](item-on-cursor-manual-qa.md) pass.
4. Implement `:tx/inv/stack` (fixes missing `:tx/stack-item` today).
5. Remove `:entity/item-on-cursor` and old handlers.

**Smallest first slice:** empty state-exit + `:tx/inv/place` and `:tx/inv/drop` that clear dragging directly — same feel in game, hack removed.

---

## Summary

**Dragging is a field. Each user action is one explicit tx (or one `inventory-op` function). Ground drop is never an FSM exit side effect.**

That removes the sim / UI / FSM boundary that made item-on-cursor hard to reason about and test.
