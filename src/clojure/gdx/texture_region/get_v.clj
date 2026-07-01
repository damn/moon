(ns clojure.gdx.texture-region.get-v
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn f [region]
  (TextureRegion/.getV region))
