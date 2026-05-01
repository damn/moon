(ns clojure.gdx.scene2d.ui.horizontal-group
  (:require [badlogic.scene2d.ui.horizontal-group :as horizontal-group]
            [clojure.gdx.scene2d.actor :as actor]))

(defmethod actor/create :ui/horizontal-group
  [opts]
  (doto (horizontal-group/create opts)
    (actor/set-opts! opts)))
