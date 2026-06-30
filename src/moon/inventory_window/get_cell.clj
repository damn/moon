(ns moon.inventory-window.get-cell
  (:require [clojure.gdx :as gdx])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [inventory-window cell]
  (->> "inventory-cell-table"
       (gdx/find-actor inventory-window)
       gdx/get-children
       (filter #(= (Actor/.getUserObject %) cell))
       first))
