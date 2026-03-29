(ns clj.api.com.badlogic.gdx.graphics.g2d.texture-region
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn width [^TextureRegion texture-region]
  (.getRegionWidth texture-region))

(defn height [^TextureRegion texture-region]
  (.getRegionHeight texture-region))
