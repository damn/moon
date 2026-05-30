(ns gdx.scenes.scene2d.ui.label
  (:require [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.actor :as actor]))

(defn create
  [opts]
  (doto (label/create opts)
    (actor/set-opts! opts)))

(defn set-text! [label text]
  (label/set-text! label text))
