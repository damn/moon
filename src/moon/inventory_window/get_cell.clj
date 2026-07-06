(ns moon.inventory-window.get-cell
  (:require
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn f [inventory-window cell]
  (->> "inventory-cell-table"
       (#(group/find-actor inventory-window %))
       get-children/f
       (filter #(= (actor/get-user-object %) cell))
       first))
