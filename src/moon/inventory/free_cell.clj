(ns moon.inventory.free-cell
  (:require [moon.item.is-stackable :as stackable?]
            [moon.inventory.valid-slots :as valid-slots]
            [moon.inventory.cells-and-items :as cells-and-items]))

(defn f [inventory slot item]
  (assert (valid-slots/v slot) (str "Slot :" (pr-str slot)))
  (first (filter (fn [[_cell cell-item]]
                   (or (stackable?/f item cell-item)
                       (nil? cell-item)))
                 (cells-and-items/f inventory slot))))
