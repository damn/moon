(ns tx.ui-remove-item
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]
            [moon.inventory-window.remove-item :as remove-item]))

(defn f
  [{:keys [ctx/stage] :as ctx} eid cell]
  (-> stage
      :stage/root
      (#(group/find-actor % "moon.ui.windows.inventory"))
      (remove-item/f cell))
  nil)
