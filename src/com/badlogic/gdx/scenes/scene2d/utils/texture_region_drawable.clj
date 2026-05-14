(ns com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable
  (:require [com.badlogic.gdx.graphics.color :as color])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn create
  ^TextureRegionDrawable
  [{:keys [drawable/texture-region
           drawable/min-size
           drawable/tint]}]
  (let [drawable (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                   (.setMinSize (min-size 0) (min-size 1)))]
    (when tint
      (.tint drawable (color/create tint)))
    drawable))
