(ns moon.inventory-window.get-cell
  (:require [gdl.get-user-object :refer [get-user-object]]
            [gdl.group.children :refer [children]]
            [gdl.group.find-actor :refer [find-actor]]))

(defn- find-inventory-window-cell [group cell]
  (first (filter #(= (get-user-object %) cell)
                 (children group))))

(defn f [inventory-window cell]
  (-> inventory-window
      (find-actor "inventory-cell-table")
      (find-inventory-window-cell cell)))
