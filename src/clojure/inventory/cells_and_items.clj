(ns clojure.inventory.cells-and-items
  (:require [clojure.valid-slots :as valid-slots]))

(defn f [inventory slot]
  (assert (valid-slots/v slot) (str "Slot :" (pr-str slot)))
  (for [[position item] (slot inventory)]
    [[slot position] item]))
