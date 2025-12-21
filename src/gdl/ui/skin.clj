(ns gdl.ui.skin
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn font [^Skin skin name]
  (.getFont skin name))

(defn create [file-handle]
  (Skin. file-handle))
