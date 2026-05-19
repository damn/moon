(ns com.badlogic.gdx.graphics.texture
  (:import (com.badlogic.gdx.graphics Texture)))

(defn create [path]
  (Texture. ^String path))
