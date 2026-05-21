(ns com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable
  (:require [gdl.graphics.color :as color]
            [gdl.graphics.g2d.texture-region :as texture-region]
            [gdl.scene2d.utils.texture-region-drawable :as texture-region-drawable])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn- set-min-size! [^TextureRegionDrawable drawable [w h]]
  (.setMinSize drawable w h))

(.bindRoot #'texture-region-drawable/create
           (fn
             [{:keys [drawable/texture-region
                      drawable/size
                      drawable/tint]}]
             (let [drawable (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                              (set-min-size! [size size]))]
               (when tint
                 (.tint drawable (color/create tint)))
               drawable)))

(.bindRoot #'texture-region-drawable/create*
           (fn
             ^TextureRegionDrawable
             [{:keys [drawable/texture-region
                      drawable/scale]}]
             (doto (TextureRegionDrawable. ^TextureRegion texture-region)
               (set-min-size! [(* scale (texture-region/width texture-region))
                               (* scale (texture-region/height texture-region))]))))
