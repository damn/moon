(ns clojure.gdx.scene2d.ui.label
  (:require [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [moon.ui.actor :as actor]
            moon.ui.label))

(defmethod actor/create :ui/label
  [opts]
  (doto (label/create opts)
    (actor/set-opts! opts)))

(extend com.badlogic.gdx.scenes.scene2d.ui.Label
  moon.ui.label/Label
  {:set-text! label/set-text!})
