(ns tx.ui-remove-item
  (:require [moon.inventory-window.remove-item :as remove-item])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn f
  [{:keys [ctx/stage] :as ctx} eid cell]
  (-> stage
      :stage/root
      (#(Group/.findActor % "moon.ui.windows.inventory"))
      (remove-item/f cell))
  nil)
