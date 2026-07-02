(ns clojure.gdx.texture-region.new
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn f
  ([texture x y w h]
   (TextureRegion. ^Texture texture (int x) (int y) (int w) (int h)))
  ([texture]
   (TextureRegion. ^Texture texture)))
