(ns com.badlogic.gdx.graphics.texture
  (:import (com.badlogic.gdx.graphics Texture
                                      Pixmap)))

(defmulti create class)

(defmethod create String [path]
  (Texture. ^String path))

(defmethod create Pixmap [pixmap]
  (Texture. ^Pixmap pixmap))
