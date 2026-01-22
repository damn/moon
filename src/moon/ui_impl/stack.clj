(ns moon.ui-impl.stack
  (:require [moon.ui.widget-group :as widget-group])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Stack)))

(defn create [opts]
  (doto (Stack.)
    (widget-group/set-opts! opts)))
