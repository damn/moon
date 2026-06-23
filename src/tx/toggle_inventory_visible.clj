(ns tx.toggle-inventory-visible
  (:require [scene2d.group.find-actor :refer [find-actor]]
            [gdl.toggle-visible :refer [toggle-visible!]]))

(defn f
  [{:keys [ctx/stage] :as ctx}]
  (-> stage
      :stage/root
      (find-actor "moon.ui.windows.inventory")
      toggle-visible!)
  nil)
