(ns clojure.get-cell
  (:require
            [gdl.actor :as actor] [clojure.scene2d.group :as group]))

(defn f [inventory-window cell]
  (->> "inventory-cell-table"
       (#(group/find-actor inventory-window %))
       group/get-children
       (filter #(= (actor/get-user-object %) cell))
       first))
