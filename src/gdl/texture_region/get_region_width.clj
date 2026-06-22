(ns gdl.texture-region.get-region-width
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn get-region-width [^TextureRegion texture-region]
  (.getRegionWidth texture-region))
