(ns gdx.skin
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn create [file-handle]
  (Skin. ^FileHandle file-handle))

(defn get-font [skin font-name]
  (.getFont ^Skin skin font-name))
