(ns moon.inventory-window.get-cell
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.group.find-actor :as find-actor]
            [clojure.gdx.group.get-children :as get-children]))

(defn f [inventory-window cell]
  (->> "inventory-cell-table"
       (#(find-actor/f inventory-window %))
       get-children/f
       (filter #(= (actor/get-user-object %) cell))
       first))
