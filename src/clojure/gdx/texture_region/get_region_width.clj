(ns clojure.gdx.texture-region.get-region-width
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn f [region]
  (TextureRegion/.getRegionWidth region))
