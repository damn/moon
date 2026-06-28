(ns moon.inventory-window.get-cell
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn f [inventory-window cell]
  (->> "inventory-cell-table"
       (#(Group/.findActor inventory-window %))
       Group/.getChildren
       (filter #(= (Actor/.getUserObject %) cell))
       first))
