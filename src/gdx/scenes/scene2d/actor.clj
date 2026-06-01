(ns gdx.scenes.scene2d.actor
  (:require [clojure.gdx.scene2d.actor.create :refer [create-actor]]
            [clojure.gdx.scene2d.actor.set-opts :refer [set-opts!]]))

(defn create [opts]
  (doto (create-actor opts)
    (set-opts! opts)))
