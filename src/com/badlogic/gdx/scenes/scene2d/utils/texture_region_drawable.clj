(ns com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable
  (:require [com.badlogic.gdx.graphics.color :as color]
            [gdl.graphics.g2d.texture-region :as texture-region])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn create [texture-region]
  (TextureRegionDrawable. ^TextureRegion texture-region))

(defn set-min-size! [^TextureRegionDrawable drawable [w h]]
  (.setMinSize drawable w h))

(defn tint! [^TextureRegionDrawable drawable color]
  (.tint drawable (color/create color)))

(defn create*
  [{:keys [drawable/texture-region
           drawable/scale]}]
  (doto (create texture-region)
    (set-min-size! [(* scale (texture-region/width texture-region))
                    (* scale (texture-region/height texture-region))])))
