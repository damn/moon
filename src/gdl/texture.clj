(ns gdl.texture
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Texture)))

(defn f [^FileHandle file]
  (Texture. file))
