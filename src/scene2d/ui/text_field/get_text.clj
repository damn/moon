(ns scene2d.ui.text-field.get-text
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextField)))

(defn f [^TextField text-field]
  (.getText text-field))
