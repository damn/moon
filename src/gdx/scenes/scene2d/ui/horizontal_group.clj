(ns gdx.scenes.scene2d.ui.horizontal-group
  (:require [clojure.gdx.scene2d.ui.horizontal-group :as horizontal-group]
            [clojure.gdx.scene2d.actor.set-opts :as actor]))

(defn create
  [opts]
  (doto (horizontal-group/create opts)
    (actor/set-opts! opts)))
