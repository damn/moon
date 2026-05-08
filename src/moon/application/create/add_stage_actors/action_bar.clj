(ns moon.application.create.add-stage-actors.action-bar
  (:require [clojure.gdx.scene2d.actor :as actor]))

(defn create [_ctx]
  (actor/create
   {:type :ui/action-bar}))
