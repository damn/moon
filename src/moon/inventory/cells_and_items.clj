(ns moon.inventory.cells-and-items
  (:require [moon.inventory.valid-slots :as valid-slots]))

(defn f [inventory slot]
  (assert (valid-slots/v slot) (str "Slot :" (pr-str slot)))
  (for [[position item] (slot inventory)]
    [[slot position] item]))
