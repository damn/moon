(ns gdx.scenes.scene2d.ui.stack
  (:require [gdx.scenes.scene2d.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Stack)))

(defn create
  [opts]
  (doto (Stack.)
    (group/set-opts! opts)))
