(ns moon.inventory-window.get-cell
  (:require [clojure.gdx.actor.get-user-object :as get-user-object]
            [clojure.gdx.group.find-actor :as find-actor]
            [clojure.gdx.group.get-children :as get-children]))

(defn f [inventory-window cell]
  (->> "inventory-cell-table"
       (#(find-actor/f inventory-window %))
       get-children/f
       (filter #(= (get-user-object/f %) cell))
       first))
