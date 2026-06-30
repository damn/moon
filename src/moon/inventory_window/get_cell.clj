(ns moon.inventory-window.get-cell
  (:require [clojure.gdx :as gdx]))

(defn f [inventory-window cell]
  (->> "inventory-cell-table"
       (gdx/find-actor inventory-window)
       gdx/get-children
       (filter #(= (gdx/get-user-object %) cell))
       first))
