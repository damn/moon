(ns clojure.gdx.texture-region.get-v2
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn f [region]
  (TextureRegion/.getV2 region))
