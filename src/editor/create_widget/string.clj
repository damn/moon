(ns editor.create-widget.string
  (:require [scene2d.ui.text-field :as text-field]
            [scene2d.ui.text-tooltip :as text-tooltip])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (Actor/.addListener (text-tooltip/create (str schema) skin))))
