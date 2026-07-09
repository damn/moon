(ns com.badlogic.gdx.graphics.texture
  (:refer-clojure :exclude [new])
  (:import
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap Texture)
           ))

(defn new [^FileHandle file-handle]
  (Texture. file-handle))

(defn new-from-pixmap [^Pixmap pixmap]
  (Texture. pixmap))
