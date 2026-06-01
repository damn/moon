(ns gdx.scenes.scene2d.ui.label
  (:require [clojure.gdx.scene2d.ui.label :as label]
            [clojure.gdx.scene2d.actor.set-opts :as actor]))

(defn create
  [opts]
  (doto (label/create opts)
    (actor/set-opts! opts)))

(defn set-text! [label text]
  (label/set-text! label text))
