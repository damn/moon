(ns game.ui.text-field
  (:require [com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]
            [gdl.scene2d.actor :as actor]
            [moon.ui.text-field]))

(defmethod actor/create :ui/text-field
  [opts]
  (doto (text-field/create opts)
    (actor/set-opts! opts)))

(extend-type com.badlogic.gdx.scenes.scene2d.ui.TextField
  moon.ui.text-field/TextField
  (text [text-field]
    (text-field/text text-field)))
