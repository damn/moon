(ns moon.ui-impl.group
  (:require [moon.ui.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn create [opts]
  (doto (Group.)
    (group/set-opts! opts)))
