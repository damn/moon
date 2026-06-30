(ns draw.texture-region
  (:require [clojure.gdx :as gdx]))

(defn f!
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/world-unit-scale]}
   texture-region
   [x y]
   & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(gdx/texture-region-get-region-width texture-region)
                                (gdx/texture-region-get-region-height texture-region)]]
                (if (= @unit-scale 1)
                  dimensions
                  (mapv (comp float (partial * world-unit-scale))
                        dimensions)))]
    (gdx/draw-texture-region! batch texture-region x y
                              :center? center?
                              :rotation rotation
                              :w w
                              :h h)))
