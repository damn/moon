(ns clojure.draw-tiled-map-tile
  (:require [clojure.tiled-map-tile :as tiled-map-tile]
            [clojure.tiled-map :as tiled-map]
            [clojure.texture-region :as texture-region]
            [clojure.texture :as texture]
            [clojure.draw! :as draw]))

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
    (aset-float verts clojure.x1/v x1)
    (aset-float verts clojure.y1/v y1)
    (aset-float verts clojure.c1/v color11)
    (aset-float verts clojure.u1/v u1)
    (aset-float verts clojure.v1/v v1)
    (aset-float verts clojure.x2/v x1)
    (aset-float verts clojure.y2/v y2)
    (aset-float verts clojure.c2/v color12)
    (aset-float verts clojure.u2/v u1)
    (aset-float verts clojure.v2/v v2)
    (aset-float verts clojure.x3/v x2)
    (aset-float verts clojure.y3/v y2)
    (aset-float verts clojure.c3/v color22)
    (aset-float verts clojure.u3/v u2)
    (aset-float verts clojure.v3/v v2)
    (aset-float verts clojure.x4/v x2)
    (aset-float verts clojure.y4/v y1)
    (aset-float verts clojure.c4/v color21)
    (aset-float verts clojure.u4/v u2)
    (aset-float verts clojure.v4/v v1)
    (draw/f batch
          (texture-region/get-texture region)
          verts
          0
          num-vertices)))
