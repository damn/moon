(ns clojure.gdx.scenes.scene2d.ui.text-button
  (:require [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [moon.ui.actor :as actor]))

(defmethod actor/create :ui/text-button
  [opts]
  (doto (text-button/create opts)
    (actor/set-opts! opts)))
