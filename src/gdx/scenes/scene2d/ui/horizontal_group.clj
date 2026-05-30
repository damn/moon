(ns gdx.scenes.scene2d.ui.horizontal-group
  (:require [com.badlogic.gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]
            [gdx.scenes.scene2d.actor :as actor]))

(defn create
  [opts]
  (doto (horizontal-group/create opts)
    (actor/set-opts! opts)))
