(ns moon.inventory-window.get-cell
  (:require [scene2d.actor.get-user-object :refer [get-user-object]]
            [scene2d.group.children :refer [children]]
            [scene2d.group.find-actor :refer [find-actor]]))

(defn f [inventory-window cell]
  (->> "inventory-cell-table"
       (find-actor inventory-window)
       children
       (filter #(= (get-user-object %) cell))
       first))
