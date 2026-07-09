(ns com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.graphics Color)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn new [^TextureRegion texture-region]
  (TextureRegionDrawable. texture-region))

(defn set-min-size! [^TextureRegionDrawable drawable min-width min-height]
  (TextureRegionDrawable/.setMinSize drawable min-width min-height))

(defn tint! [^TextureRegionDrawable drawable ^Color color]
  (.tint drawable color))
