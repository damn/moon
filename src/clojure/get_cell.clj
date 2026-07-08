(ns clojure.get-cell
  (:require
            [clojure.scene2d.actor.get-user-object] [clojure.scene2d.group :as group]))

(defn f [inventory-window cell]
  (->> "inventory-cell-table"
       (#(group/find-actor inventory-window %))
       group/get-children
       (filter #(= (clojure.scene2d.actor.get-user-object/f %) cell))
       first))
