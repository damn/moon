(ns moon.tx.show-message
  (:require [moon.ui :as ui]))

(defn do!
  [{:keys [ctx/stage] :as ctx} message]
  (ui/show-text-message! stage message)
  ctx)
