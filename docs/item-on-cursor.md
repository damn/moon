# Item on cursor

Player inventory drag-and-drop crosses **FSM state**, **sim txs**, **input**, and **UI**.
This doc describes current behaviour as of the flat `clojure.*` layout.
Refactors should keep these tests green before changing structure.

**Proposed simpler design:** [item-on-cursor-redesign.md](item-on-cursor-redesign.md)

## States (player FSM)

From `clojure.fsms`:

| State | Relevant transitions |
|-------|----------------------|
| `:player-idle` | `:pickup-item` → `:player-item-on-cursor` |
| `:player-item-on-cursor` | `:drop-item` or `:dropped-item` → `:player-idle` |

Both `:drop-item` and `:dropped-item` return to idle; they differ in **who** emits them and **what runs on state-exit**.

## Data on the entity

| Key | Meaning |
|-----|---------|
| `:entity/inventory` | Nested map keyed by `[slot [x y]]` → item map |
| `:entity/item-on-cursor` | Item held on cursor (only while in `:player-item-on-cursor`) |
| `:player-item-on-cursor` | FSM state component `{:item …}` (parallel to `:entity/item-on-cursor`) |

Enter associates `:entity/item-on-cursor`; exit reads it to spawn on the ground.

## Entry points (four paths)

```
                    ┌─────────────────────────────────────┐
                    │         :player-idle                │
                    └──────────────┬──────────────────────┘
                                   │
          click inventory cell     │  (moon.clicked-inventory-cell.player-idle)
                                   ▼
                    pickup-item event + remove-item from cell
                                   │
                                   ▼
                    ┌─────────────────────────────────────┐
                    │   :player-item-on-cursor              │
                    └──────────────┬──────────────────────┘
                                   │
         ┌─────────────────────────┼─────────────────────────┐
         │                         │                         │
  click empty/valid cell    click occupied cell      click world (not UI)
  (inventory handler)       (swap / stack)           (k-handle-input)
         │                         │                         │
         ▼                         ▼                         ▼
  dissoc + set-item +        dissoc + swap txs +        :drop-item event
  :dropped-item event        :dropped-item (+ pickup)   (item still on cursor)
         │                         │                         │
         └─────────────────────────┴─────────────────────────┘
                                   │
                          :tx/state-exit (player-item-on-cursor)
                                   │
                    if :entity/item-on-cursor still set → spawn on ground
                    if already dissoc'd → no ground spawn (inventory path)
```

## Path 1 — Pick up (idle → item on cursor)

**Handler:** `moon.clicked-inventory-cell.player-idle`

1. `[:tx/sound "bfxr_takeit"]`
2. `[:tx/event eid :pickup-item item]` — FSM → `:player-item-on-cursor`
3. `[:tx/remove-item eid cell]` — clears inventory cell (+ UI)

**State enter** (`moon.state-enter.player-item-on-cursor`):

- `[:tx/assoc eid :entity/item-on-cursor item]`

## Path 2 — Put in inventory (item on cursor → idle)

**Handler:** `moon.clicked-inventory-cell.player-item-on-cursor`

Must **`dissoc` `:entity/item-on-cursor` before** `[:tx/event eid :dropped-item]`.
Otherwise **state-exit** still sees the item and **also** spawns it on the ground.

### Empty valid cell

1. sound `bfxr_itemput`
2. `dissoc` item-on-cursor
3. `set-item` into cell
4. `:dropped-item` event → idle

### Stackable cell

Same pattern, but emits `[:tx/stack-item …]` — **see known gap below**.

### Swap with occupied cell

1. dissoc, remove-item, set-item (cursor item into cell)
2. `:dropped-item` → idle
3. `:pickup-item` with cell's old item → back on cursor

## Path 3 — Drop on ground (world click)

**Handler:** `k-handle-input.player-item-on-cursor`

When left mouse pressed and **not** `mouseover-actor` (not over UI):

- `[:tx/event eid :drop-item]` → idle

**State exit** (`moon.state-exit.player-item-on-cursor`):

- If `:entity/item-on-cursor` present:
  - sound `bfxr_itemputground`
  - dissoc item-on-cursor
- `spawn-item` at player's feet (`:body/position`)

**Placement** (`item-place-position`): player body center — no mouse / reach clamp.

## Path 4 — Render preview

**Handler:** `moon.render.player-item-on-cursor`

Draws item texture at `item-place-position` unless `mouseover-actor` (mouse over UI).

## Tx chain notes

- `:tx/set-item` / `:tx/remove-item` also emit `:tx/ui-set-item` / `:tx/ui-remove-item` for player.
- `:tx/event` expands into FSM update + `:tx/state-exit` + `:tx/state-enter` **before** remaining txs in the queue (`actions!` prepends).
- Inventory handlers rely on ordering: **clear cursor sim state, then** transition FSM.

## Known gaps

| Issue | Location |
|-------|----------|
| `:tx/stack-item` emitted but **not implemented** in `tx.clj` | stacking from cursor will fail at `do!` |
| `:tx/pickup-item` stack branch commented out | `tx.clj` |
| Swap path TODO: pickup from cursor state could be unified | `player_item_on_cursor.clj` |
| Double source of truth: `:entity/item-on-cursor` vs `:player-item-on-cursor` state map | enter/exit vs FSM component |

## Safe refactor order

1. Keep handler return-value tests (`test/moon/item_on_cursor_test.clj`).
2. Implement or remove `:tx/stack-item`.
3. Split “inventory put” vs “ground drop” in state-exit (explicit tx, no dissoc-before-event hack).
4. Pass **frame** snapshot (`world-mouse-position`, ui-hit) instead of full `ctx` into exit/render.

## Manual QA

In-game checklist (TAKE, PLACE, SWAP, DROP, UI guards): [item-on-cursor-manual-qa.md](item-on-cursor-manual-qa.md)
