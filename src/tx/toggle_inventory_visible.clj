(ns tx.toggle-inventory-visible
  (:require [clojure.gdx.scene2d.group.find-actor :refer [find-actor]]
            [clojure.gdx.scene2d.actor.toggle-visible :refer [toggle-visible!]]))

(defn f
  [{:keys [ctx/stage] :as ctx}]
  (-> stage
      :stage/root
      (find-actor "moon.ui.windows.inventory")
      toggle-visible!)
  nil)
