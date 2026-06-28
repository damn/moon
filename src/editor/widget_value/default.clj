(ns editor.widget-value.default
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f
  [_  widget _schemas]
  ((Actor/.getUserObject widget) 1))
