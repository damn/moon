(ns com.badlogic.gdx.graphics.g2d.texture-region
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn create [^Texture texture x y w h]
  (TextureRegion. texture (int x) (int y) (int w) (int h)))

(def width  TextureRegion/.getRegionWidth)
(def height TextureRegion/.getRegionHeight)
