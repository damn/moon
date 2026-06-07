(ns gdx.scenes.scene2d.utils.texture-region-drawable
  (:require [com.badlogic.gdx.graphics.g2d.texture-region.get-region-height :refer [get-region-height]]
            [com.badlogic.gdx.graphics.g2d.texture-region.get-region-width :refer [get-region-width]]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as drawable])
  (:import (com.badlogic.gdx.graphics Color)))

(defn create
  [{:keys [drawable/texture-region
           drawable/size
           drawable/tint]}]
  (let [drawable (doto (drawable/create texture-region)
                   (drawable/set-min-size! size size))]
    (when-let [[r g b a] tint]
      (drawable/tint! drawable (Color. r g b a)))
    drawable))

(defn create*
  [{:keys [drawable/texture-region
           drawable/scale]}]
  (doto (drawable/create texture-region)
    (drawable/set-min-size! (* scale (get-region-width texture-region))
                            (* scale (get-region-height texture-region)))))
