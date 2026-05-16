(ns clojure.gdx.scenes.scene2d.ui.horizontal-group
  (:require [com.badlogic.gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]
            [moon.ui.actor :as actor]))

(defmethod actor/create :ui/horizontal-group
  [opts]
  (doto (horizontal-group/create opts)
    (actor/set-opts! opts)))
