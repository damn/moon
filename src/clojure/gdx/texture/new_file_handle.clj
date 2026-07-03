(ns clojure.gdx.texture.new-file-handle
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Texture)))

(defn f [^FileHandle file-handle]
  (Texture. file-handle))
