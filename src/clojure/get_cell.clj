(ns clojure.get-cell
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor] [clojure.scene2d.group :as group]))

(defn f [inventory-window cell]
  (->> "inventory-cell-table"
       (#(group/find-actor inventory-window %))
       group/get-children
       (filter #(= (actor/getUserObject %) cell))
       first))
