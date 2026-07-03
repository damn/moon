(ns draw.texture-region
  (:require [clojure.gdx.batch.draw-texture-region! :as draw-texture-region!]
            [clojure.gdx.texture-region.get-region-height :as get-region-height]
            [clojure.gdx.texture-region.get-region-width :as get-region-width]))

(defn f!
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/world-unit-scale]}
   texture-region
   [x y]
   & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(get-region-width/f texture-region)
                                (get-region-height/f texture-region)]]
                (if (= @unit-scale 1)
                  dimensions
                  (mapv (comp float (partial * world-unit-scale))
                        dimensions)))]
    (if center?
      (draw-texture-region!/f batch
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
      (draw-texture-region!/f batch texture-region x y w h))))
