(ns moon.inventory
  (:require [clojure.inventory.free-cell :as free-cell]
            [moon.item :as item]))

(defn can-pickup-item? [inventory item]
  (assert (item/valid? item))
  (or (free-cell/f inventory (:item/slot item) item)
      (free-cell/f inventory :inventory.slot/bag item)))
