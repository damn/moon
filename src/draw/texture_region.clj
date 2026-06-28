(ns draw.texture-region
  (:require [texture-region.get-region-height :refer [get-region-height]]
            [texture-region.get-region-width :refer [get-region-width]])
  (:import (com.badlogic.gdx.graphics.g2d Batch)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn f!
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/world-unit-scale]}
   ^TextureRegion texture-region
   [x y]
   & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(get-region-width  texture-region)
                                (get-region-height texture-region)]]
                (if (= @unit-scale 1)
                  dimensions
                  (mapv (comp float (partial * world-unit-scale))
                        dimensions)))]
    (if center?
      (.draw ^Batch batch
             texture-region
             (- (float x) (/ (float w) 2))
             (- (float y) (/ (float h) 2))
             (/ (float w) 2)
             (/ (float h) 2)
             w
             h
             1
             1
             (or rotation 0))
      (.draw ^Batch batch texture-region (float x) (float y) (float w) (float h)))))
