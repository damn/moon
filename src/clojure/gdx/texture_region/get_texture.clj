(ns clojure.gdx.texture-region.get-texture
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn f [region]
  (TextureRegion/.getTexture region))
