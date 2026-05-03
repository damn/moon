(ns clojure.gdx.scene2d.ui.text-field
  (:require [com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]
            [clojure.gdx.scene2d.actor :as actor]))

(defmethod actor/create :ui/text-field
  [opts]
  (doto (text-field/create opts)
    (actor/set-opts! opts)))

(def text text-field/text)
