(ns moon.tx.toggle-inventory-visible
  (:require [moon.ui :as ui]))

(defn do!
  [{:keys [ctx/stage] :as ctx}]
  (ui/toggle-inventory-visible! stage)
  ctx)
