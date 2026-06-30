(ns moon.inventory.is-applies-modifiers)

(defn f [[slot _]]
  (not= :inventory.slot/bag slot))
