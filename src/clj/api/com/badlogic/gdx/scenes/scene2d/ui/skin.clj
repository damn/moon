(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.skin
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn create [file-handle]
  (Skin. file-handle))

(defn font [^Skin skin name]
  (.getFont skin name))
