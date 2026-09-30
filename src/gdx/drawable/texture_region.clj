(ns gdx.drawable.texture-region
  (:import (com.badlogic.gdx.graphics Color)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn create [texture-region]
  (TextureRegionDrawable. ^TextureRegion texture-region))

(defn set-min-size! [texture-region-drawable min-w min-h]
  (.setMinSize ^TextureRegionDrawable texture-region-drawable min-w min-h))

(defn tint! [texture-region-drawable color]
  (.tint ^TextureRegionDrawable texture-region-drawable ^Color color))
