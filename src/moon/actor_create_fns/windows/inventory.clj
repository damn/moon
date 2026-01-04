(ns moon.actor-create-fns.windows.inventory
  (:require [moon.application.create.ui.inventory-window :as inventory-window]))

(defn create [ctx]
  (inventory-window/create ctx))
