(ns moon.application.create.add-stage-actors.action-bar
  (:require [moon.ui.actor :as actor]))

(defn create [_ctx]
  (actor/create
   {:type :ui/action-bar}))
