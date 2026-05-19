(ns com.badlogic.gdx.graphics.g2d.texture-region
  (:require [gdl.graphics.g2d.texture-region :as texture-region])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn create
  ([^Texture texture]
   (TextureRegion. texture))
  ([^Texture texture x y w h]
   (TextureRegion. texture (int x) (int y) (int w) (int h))))

(extend-type TextureRegion
  texture-region/TextureRegion
  (width [this]
    (.getRegionWidth this))

  (height [this]
    (.getRegionHeight this)))
