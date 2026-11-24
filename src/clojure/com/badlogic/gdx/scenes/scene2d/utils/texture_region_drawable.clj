(ns clojure.com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable
  "Drawable for a [[clojure.com.badlogic.gdx.graphics.g2d.texture-region]]."
  (:import (com.badlogic.gdx.graphics Color)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn create [^TextureRegion texture-region]
  (TextureRegionDrawable. texture-region))

(defn set-min-size! [^TextureRegionDrawable drawable width height]
  (.setMinSize drawable width height))

(defn tint
  "Creates a new drawable that renders the same as this drawable tinted the specified color."
  [^TextureRegionDrawable drawable ^Color color]
  (.tint drawable color))
