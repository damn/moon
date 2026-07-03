(ns clojure.gdx.text-field.get-text
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextField)))

(defn f [^TextField text-field]
  (TextField/.getText text-field))
