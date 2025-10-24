(ns clojure.gdx.scene2d.utils.texture-region-drawable
  "Drawable for a `TextureRegion`."
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn create [^TextureRegion texture-region]
  (TextureRegionDrawable. texture-region))

(defn tint
  "Creates a new drawable that renders the same as this drawable tinted the specified color."
  [^TextureRegionDrawable texture-region-drawable color]
  (.tint texture-region-drawable color))
