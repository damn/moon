(ns clojure.draw-tiled-map-tile
  (:require [clojure.gdx.graphics.g2d.batch :as batch]
            [clojure.tiled-map-tile :as tiled-map-tile]
            [clojure.tiled-map :as tiled-map]
            [clojure.texture-region :as texture-region]
            [clojure.texture :as texture]))

(defn f!
  [x
   y
   tile
   unit-scale
   color-setter
   batch-color
   verts
   batch
   num-vertices]
  (let [region (tiled-map-tile/get-texture-region tile)
        x1 (+ x (* (tiled-map-tile/get-offset-x tile) unit-scale))
        y1 (+ y (* (tiled-map-tile/get-offset-y tile) unit-scale))
        x2 (+ x1 (* (texture-region/get-region-width region) unit-scale))
        y2 (+ y1 (* (texture-region/get-region-height region) unit-scale))
        u1 (texture-region/get-u region)
        v1 (texture-region/get-v2 region)
        u2 (texture-region/get-u2 region)
        v2 (texture-region/get-v region)
        color11 (float (color-setter batch-color x1 y1))
        color12 (float (color-setter batch-color x1 y2))
        color22 (float (color-setter batch-color x2 y2))
        color21 (float (color-setter batch-color x2 y1))]
    (doseq [[[x-idx y-idx c-idx u-idx v-idx] [x y c u v]]
            (map vector batch/vertex-indices
                       [[x1 y1 color11 u1 v1]
                        [x1 y2 color12 u1 v2]
                        [x2 y2 color22 u2 v2]
                        [x2 y1 color21 u2 v1]])]
      (aset-float verts x-idx x)
      (aset-float verts y-idx y)
      (aset-float verts c-idx c)
      (aset-float verts u-idx u)
      (aset-float verts v-idx v))
    (batch/draw-vertices! batch
                          (texture-region/get-texture region)
                          verts
                          0
                          num-vertices)))
