(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.stack
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Stack)))

(defn create [opts]
  (doto (Stack.)
    (widget-group/set-opts! opts)))
