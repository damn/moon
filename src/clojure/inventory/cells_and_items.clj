(ns clojure.inventory.cells-and-items
  (:require [clojure.inventory.slot :as slot]))

(defn f [inventory slot]
  (assert (slot/v slot) (str "Slot :" (pr-str slot)))
  (for [[position item] (slot inventory)]
    [[slot position] item]))
