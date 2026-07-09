# Item on cursor — manual QA

How to verify inventory drag-and-drop in the live game.

Technical flow and handler details: [item-on-cursor.md](item-on-cursor.md)  
Automated handler tests: `test/moon/item_on_cursor_test.clj` (`lein test :only moon.item-on-cursor-test`)

## Setup

```bash
lein dev
```

1. Game starts on the Vampire map — player spawns with gear in inventory.
2. Press **`I`** to open inventory (bottom-right).
3. Dev menu → **Help** for controls; **`E`** toggles entity info while debugging.

While holding an item, the game **pauses** (`:player-item-on-cursor` is a pausing state). That is expected.

**Cursor:** grab-hand cursor while an item is on the cursor.

---

## Checklist

| # | Action | Steps | Pass if | Unit test |
|---|--------|-------|---------|-----------|
| **TAKE** | Pick up from inventory | `I` → click item in a filled slot (weapon, shield, etc.) | Item leaves cell; grab cursor; take sound; item preview follows mouse on map (not over UI) | `idle-click-pickup-txs` |
| **PLACE** | Put in empty valid slot | While holding → click empty **weapon** slot with a **weapon** (or bag cell for bag items) | Item in cell; cursor normal; **no** duplicate on ground | `cursor-click-empty-cell-order` |
| **SWAP** | Switch with occupied slot | Hold weapon → click occupied weapon slot with a different weapon | Previous cell item back on cursor; new item in cell; at most **one** ground item | `cursor-click-swap` |
| **STACK** | Stack stackables | Two stackable bag items (if available) | ⚠️ **Likely broken** — `:tx/stack-item` not implemented in `tx.clj` | `cursor-click-stack-emits-stack-tx` |
| **DROP** | Drop on ground | Hold item → left-click **map** (not UI) | Item appears **at player's feet**; put-ground sound | `cursor-exit-spawns-when-item-on-cursor`, `cursor-input-drop-on-world-click` |
| **NO DROP ON UI** | Click UI while holding | Hold item → click inventory chrome, dev menu, or other UI | Item **still** on cursor; **no** ground item | `cursor-input-no-drop-over-ui` |
| **NO PLACE invalid slot** | Wrong slot type | Hold weapon → click ring/neck slot (if empty) | Nothing happens; still holding | `cursor-click-invalid-slot` |
| **NO DOUBLE DROP** | Place in inventory (regression) | PLACE into empty cell | Exactly **one** item in cell; **zero** on ground nearby | `cursor-exit-skips-when-already-cleared` |
| **PREVIEW** | World ghost while dragging | Hold item → move mouse over map vs over inventory | Preview on map; **hidden** over UI | manual only (render) |
| **BAG HIGHLIGHT** | Valid/invalid drop hint | Hold item → hover bag slots | Green = can drop; red = invalid | manual only (inventory draw) |

---

## Suggested session (~5 min)

1. **`I`** → **TAKE** sword from weapon slot.
2. Hover map → see preview; hover inventory → preview gone.
3. **PLACE** into empty bag slot. Walk the map — confirm no sword on the ground.
4. **TAKE** again → **SWAP** by clicking an occupied weapon slot.
5. **TAKE** → **DROP** on ground (click dirt, not a window).
6. **TAKE** → try **DROP** by clicking dev menu title — item should stay on cursor.
7. Optional: **`E`** entity info — `:entity/item-on-cursor` only while holding.

---

## When something fails

| Symptom | Likely cause |
|---------|----------------|
| Item on ground **and** in inventory | State-exit double-drop (`dissoc` before `:dropped-item` ordering) |
| Click ground, nothing happens | Mouse still over a UI actor (`mouseover-actor`) |
| Stack click → error / assert | Missing `:tx/stack-item` handler |
| Click empty cell in idle, nothing | Expected — no item in that cell |

Right-click tile or entity opens the debug data window — useful for inspecting player entity keys.

---

## Automated vs manual

| Layer | Command | Covers |
|-------|---------|--------|
| Handler txs | `lein test :only moon.item-on-cursor-test` | TAKE, PLACE, SWAP, DROP txs, invalid slot, UI drop guard |
| Full suite | `lein test` | Above + rest of project |
| Live game | This checklist | Cursor, sound, preview, bag colors, no double spawn |

Automated tests assert **which txs handlers return**, not UI textures, sounds, or ground sprites. Use this doc for those.
