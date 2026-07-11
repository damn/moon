(ns moon.inventory.cell)

(defn applies-modifiers? [[slot _]]
  (not= :inventory.slot/bag slot))
