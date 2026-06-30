(ns tx.ui-remove-item
  (:require [clojure.gdx :as gdx]
            [moon.inventory-window.remove-item :as remove-item]))

(defn f
  [{:keys [ctx/stage] :as ctx} eid cell]
  (-> stage
      :stage/root
      (gdx/find-actor "moon.ui.windows.inventory")
      (remove-item/f cell))
  nil)
