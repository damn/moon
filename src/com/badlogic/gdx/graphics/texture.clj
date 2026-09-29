(ns com.badlogic.gdx.graphics.texture
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.graphics Texture
                                      TextureData)))

(defn new [^TextureData data]
  (Texture. data))
