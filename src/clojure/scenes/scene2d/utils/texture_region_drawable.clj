(ns clojure.scenes.scene2d.utils.texture-region-drawable
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn create [texture-region]
  (TextureRegionDrawable. ^TextureRegion texture-region))

(defn set-min-size! [^TextureRegionDrawable drawable width height]
  (.setMinSize drawable width height))

(defn tint! [^TextureRegionDrawable drawable color]
  (.tint drawable color))
