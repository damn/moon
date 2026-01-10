(ns gdl.ui.text-field
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField)))

(defn create [text ^Skin skin]
  (TextField. (str text) skin))

(def text TextField/.getText)
