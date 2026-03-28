(ns clj.api.com.badlogic.gdx.graphics.texture
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn region [^Texture texture x y width height]
  (TextureRegion. texture x y width height))
