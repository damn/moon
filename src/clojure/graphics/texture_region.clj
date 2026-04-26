(ns clojure.graphics.texture-region
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn width [texture-region]
  (.getRegionWidth texture-region))

(defn height [texture-region]
  (.getRegionHeight texture-region))

