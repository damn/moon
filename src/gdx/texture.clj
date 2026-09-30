(ns gdx.texture
  (:import (com.badlogic.gdx.graphics Texture
                                      TextureData)))

(defn create [texture-data]
  (Texture. ^TextureData texture-data))
