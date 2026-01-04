(ns moon.actor-create-fns.windows.info-window
  (:require [moon.application.create.ui.entity-info-window-config :as entity-info-window-config]
            [moon.ui.info-window :as info-window]))

(defn create
  [{:keys [ctx/skin]
    :as ctx}]
  (info-window/create skin (entity-info-window-config/create ctx)))
