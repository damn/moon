(ns clojure.ui-remove-item
  (:require [clojure.group :as group]
            [clojure.inventory-window-remove-item :as remove-item]))

(defn f
  [{:keys [ctx/stage] :as ctx} eid cell]
  (-> stage
      :stage/root
      (#(group/find-actor % "moon.ui.windows.inventory"))
      (remove-item/f cell))
  nil)
