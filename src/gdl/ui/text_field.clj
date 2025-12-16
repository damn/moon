(ns gdl.ui.text-field
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextField)))

(defn create [text skin]
  (TextField. (str text) skin))

(def text TextField/.getText)
