(ns clojure.get-cell
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor] [com.badlogic.gdx.scenes.scene2d.group :as group]))

(defn f [inventory-window cell]
  (->> "inventory-cell-table"
       (#(group/findActor inventory-window %))
       group/getChildren
       (filter #(= (actor/getUserObject %) cell))
       first))
