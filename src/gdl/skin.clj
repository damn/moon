(ns gdl.skin
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn font [^Skin skin name]
  (.getFont skin name))
