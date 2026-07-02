(ns moon.inventory-window.get-cell
  (:require [clojure.gdx.actor.get-user-object :as get-user-object])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn f [inventory-window cell]
  (->> "inventory-cell-table"
       (#(Group/.findActor inventory-window %))
       Group/.getChildren
       (filter #(= (get-user-object/f %) cell))
       first))
