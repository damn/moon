(ns moon.inventory-window.get-cell
  (:require [scene2d.actor.get-user-object :refer [get-user-object]]
            [scene2d.group.children :refer [children]]
            [scene2d.group.find-actor :refer [find-actor]]))

(defn- find-inventory-window-cell [group cell]
  (first (filter #(= (get-user-object %) cell)
                 (children group))))

(defn f [inventory-window cell]
  (-> inventory-window
      (find-actor "inventory-cell-table")
      (find-inventory-window-cell cell)))
