(ns com.badlogic.gdx.graphics.texture
  (:import (com.badlogic.gdx.graphics Texture
                                      Pixmap)))

(defmulti create class)

(defmethod create Pixmap [^Pixmap pixmap]
  (Texture. pixmap))

(defmethod create String [^String path]
  (Texture. path))
