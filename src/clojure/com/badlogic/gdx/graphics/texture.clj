(ns clojure.com.badlogic.gdx.graphics.texture
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap Texture)))

(defn create [^FileHandle file-handle]
  (Texture. file-handle))

(defn from-pixmap [^Pixmap pixmap]
  (Texture. pixmap))
