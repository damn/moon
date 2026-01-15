(ns moon.draw.texture-region
  (:import (com.badlogic.gdx.graphics.g2d Batch
                                          TextureRegion)))

(defn do!
  [{:keys [^Batch graphics/batch
           graphics/unit-scale
           graphics/world-unit-scale]}
   ^TextureRegion texture-region
   [x y]
   & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(.getRegionWidth  texture-region)
                                (.getRegionHeight texture-region)]]
                (if (= @unit-scale 1)
                  dimensions
                  (mapv (comp float (partial * world-unit-scale))
                        dimensions)))]
    (if center?
      (.draw batch
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
      (.draw batch
             texture-region
             (float x)
             (float y)
             (float w)
             (float h)))))
