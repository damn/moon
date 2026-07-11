(ns clojure.inventory.free-cell
  (:require [clojure.inventory.slot :as slot]
            [moon.item :as item]
            [clojure.inventory.cells-and-items :as cells-and-items]))

(defn f [inventory slot item]
  (assert (slot/v slot) (str "Slot :" (pr-str slot)))
  (first (filter (fn [[_cell cell-item]]
                   (or (item/stackable? item cell-item)
                       (nil? cell-item)))
                 (cells-and-items/f inventory slot))))
