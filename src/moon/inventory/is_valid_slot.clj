(ns moon.inventory.is-valid-slot)

(defn f [[slot _] item]
  (or (= :inventory.slot/bag slot)
      (= (:item/slot item) slot)))
