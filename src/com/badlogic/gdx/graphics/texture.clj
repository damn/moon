(ns com.badlogic.gdx.graphics.texture
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap Texture)))

(defn new [^Pixmap pixmap]
  (Texture. pixmap))

(defn new-file-handle [^FileHandle file-handle]
  (Texture. file-handle))
