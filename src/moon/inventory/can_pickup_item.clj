(ns moon.inventory.can-pickup-item
  (:require [moon.inventory.free-cell :as free-cell]
            [moon.item.is-valid :as valid?]))

(defn f? [inventory item]
  (assert (valid?/f item))
  (or (free-cell/f inventory (:item/slot item)   item)
      (free-cell/f inventory :inventory.slot/bag item)))
