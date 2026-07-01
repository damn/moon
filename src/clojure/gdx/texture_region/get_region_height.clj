(ns clojure.gdx.texture-region.get-region-height
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn f [region]
  (TextureRegion/.getRegionHeight region))
