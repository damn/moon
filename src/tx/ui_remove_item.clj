(ns tx.ui-remove-item
  (:require [clojure.scenes.scene2d.group.find-actor :refer [find-actor]]
            [moon.inventory-window.remove-item :as remove-item]))

(defn f
  [{:keys [ctx/stage] :as ctx} eid cell]
  (-> stage
      :stage/root
      (find-actor "moon.ui.windows.inventory")
      (remove-item/f cell))
  nil)
