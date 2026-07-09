(ns clojure.batch.draw-tiled-map-tile
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
    (aset-float verts batch/X1 x1)
    (aset-float verts batch/Y1 y1)
    (aset-float verts batch/C1 color11)
    (aset-float verts batch/U1 u1)
    (aset-float verts batch/V1 v1)
    (aset-float verts batch/X2 x1)
    (aset-float verts batch/Y2 y2)
    (aset-float verts batch/C2 color12)
    (aset-float verts batch/U2 u1)
    (aset-float verts batch/V2 v2)
    (aset-float verts batch/X3 x2)
    (aset-float verts batch/Y3 y2)
    (aset-float verts batch/C3 color22)
    (aset-float verts batch/U3 u2)
    (aset-float verts batch/V3 v2)
    (aset-float verts batch/X4 x2)
    (aset-float verts batch/Y4 y1)
    (aset-float verts batch/C4 color21)
    (aset-float verts batch/U4 u2)
    (aset-float verts batch/V4 v1)
    (batch/draw! batch
                          (texture-region/get-texture region)
                          verts
                          0
                          num-vertices)))
