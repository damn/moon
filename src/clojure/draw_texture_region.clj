(ns clojure.draw-texture-region
  (:require [clojure.texture-region :as texture-region]
            [clojure.texture :as texture]
            [clojure.batch :as batch]))

(defn f!
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/world-unit-scale]}
   texture-region
   [x y]
   & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(texture-region/get-region-width texture-region)
                                (texture-region/get-region-height texture-region)]]
                (if (= @unit-scale 1)
                  dimensions
                  (mapv (comp float (partial * world-unit-scale))
                        dimensions)))]
    (if center?
      (batch/draw-texture-region! batch
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
      (batch/draw-texture-region! batch texture-region x y w h))))
