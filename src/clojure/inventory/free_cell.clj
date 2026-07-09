(ns clojure.inventory.free-cell
  (:require [clojure.inventory.slot :as slot]
            [clojure.is-stackable :as stackable?]
            [clojure.inventory.cells-and-items :as cells-and-items]))

(defn f [inventory slot item]
  (assert (slot/v slot) (str "Slot :" (pr-str slot)))
  (first (filter (fn [[_cell cell-item]]
                   (or (stackable?/f item cell-item)
                       (nil? cell-item)))
                 (cells-and-items/f inventory slot))))
