(ns tx.ui-remove-item
  (:require [clojure.gdx.group.find-actor :as find-actor]
            [moon.inventory-window.remove-item :as remove-item]))

(defn f
  [{:keys [ctx/stage] :as ctx} eid cell]
  (-> stage
      :stage/root
      (#(find-actor/f % "moon.ui.windows.inventory"))
      (remove-item/f cell))
  nil)
