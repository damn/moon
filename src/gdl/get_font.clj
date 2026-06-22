(ns gdl.get-font
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn f [^Skin skin name]
  (.getFont skin name))
