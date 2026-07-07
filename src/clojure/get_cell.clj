(ns clojure.get-cell
  (:require
            [clojure.get-user-object] [clojure.group :as group]))

(defn f [inventory-window cell]
  (->> "inventory-cell-table"
       (#(group/find-actor inventory-window %))
       group/get-children
       (filter #(= (clojure.get-user-object/f %) cell))
       first))
