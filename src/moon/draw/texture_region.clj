(ns moon.draw.texture-region
  (:require [clj.api.com.badlogic.gdx.graphics.g2d.batch :as batch]
            [clj.api.com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]))

(defn do!
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/world-unit-scale]}
   texture-region
   [x y]
   & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(texture-region/width  texture-region)
                                (texture-region/height texture-region)]]
                (if (= @unit-scale 1)
                  dimensions
                  (mapv (comp float (partial * world-unit-scale))
                        dimensions)))]
    (if center?
      (batch/draw! batch
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
      (batch/draw! batch texture-region x y w h))))
