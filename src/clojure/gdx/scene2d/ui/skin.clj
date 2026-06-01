(ns clojure.gdx.scene2d.ui.skin
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn create [^FileHandle file-handle]
  (Skin. file-handle))

(defn font [^Skin skin name]
  (.getFont skin name))
