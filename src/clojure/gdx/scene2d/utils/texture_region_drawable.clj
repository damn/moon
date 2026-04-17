(ns clojure.gdx.scene2d.utils.texture-region-drawable
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn create [^TextureRegion texture-region]
  (TextureRegionDrawable. texture-region))

(defn tint [^TextureRegionDrawable texture-region-drawable color]
  (.tint texture-region-drawable color))
