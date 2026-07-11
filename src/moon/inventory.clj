(ns moon.inventory
  (:require [clojure.inventory.free-cell :as free-cell]
            [clojure.item-is-valid :as valid?]))

(defn can-pickup-item? [inventory item]
  (assert (valid?/f item))
  (or (free-cell/f inventory (:item/slot item) item)
      (free-cell/f inventory :inventory.slot/bag item)))
