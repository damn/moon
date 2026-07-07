(ns clojure.get-cell
  (:require [clojure.group :as group]
            [clojure.actor :as actor]))

(defn f [inventory-window cell]
  (->> "inventory-cell-table"
       (#(group/find-actor inventory-window %))
       group/get-children
       (filter #(= (actor/get-user-object %) cell))
       first))
