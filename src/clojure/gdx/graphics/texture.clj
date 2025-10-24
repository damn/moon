(ns clojure.gdx.graphics.texture
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Texture)))

(defmulti create class)

(defmethod create Pixmap [^Pixmap pixmap]
  (Texture. pixmap))

(defmethod create String [^String path]
  (Texture. path))

(defmethod create FileHandle [^FileHandle file-handle]
  (Texture. file-handle))
