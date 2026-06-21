(ns clojure.graphics.texture
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Texture)))

(defn create [^FileHandle file-handle]
  (Texture. file-handle))
