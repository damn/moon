(ns clojure.inventory.can-pickup-item
  (:require [clojure.inventory.free-cell :as free-cell]
            [clojure.item-is-valid :as valid?]))

(defn f? [inventory item]
  (assert (valid?/f item))
  (or (free-cell/f inventory (:item/slot item)   item)
      (free-cell/f inventory :inventory.slot/bag item)))
