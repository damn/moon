(ns editor.widget-value.string
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextField)))

(defn f
  [_ widget _schemas]
  (TextField/.getText widget))
