(ns clojure.gdx.scene2d.ui.label
  (:require [com.badlogic.gdx.scene2d.ui.label :as label]
            [clojure.gdx.scene2d.actor :as actor]))

(defmethod actor/create :ui/label
  [opts]
  (doto (label/create opts)
    (actor/set-opts! opts)))

(def set-text! label/set-text!)
