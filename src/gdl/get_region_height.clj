(ns gdl.get-region-height
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn get-region-height [^TextureRegion texture-region]
  (.getRegionHeight texture-region))
