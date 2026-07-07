(ns clojure.draw-tiled-map-tile
  (:require [clojure.tiled-map-tile :as tiled-map-tile]
            [clojure.tiled-map :as tiled-map]
            [clojure.texture-region :as texture-region]
            [clojure.texture :as texture]
            [clojure.draw! :as draw]
            [clojure.x1 :as vert-x1]
            [clojure.y1 :as vert-y1]
            [clojure.c1 :as vert-c1]
            [clojure.u1 :as vert-u1]
            [clojure.v1 :as vert-v1]
            [clojure.x2 :as vert-x2]
            [clojure.y2 :as vert-y2]
            [clojure.c2 :as vert-c2]
            [clojure.u2 :as vert-u2]
            [clojure.v2 :as vert-v2]
            [clojure.x3 :as vert-x3]
            [clojure.y3 :as vert-y3]
            [clojure.c3 :as vert-c3]
            [clojure.u3 :as vert-u3]
            [clojure.v3 :as vert-v3]
            [clojure.x4 :as vert-x4]
            [clojure.y4 :as vert-y4]
            [clojure.c4 :as vert-c4]
            [clojure.u4 :as vert-u4]
            [clojure.v4 :as vert-v4]))

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
    (aset-float verts vert-x1/v x1)
    (aset-float verts vert-y1/v y1)
    (aset-float verts vert-c1/v color11)
    (aset-float verts vert-u1/v u1)
    (aset-float verts vert-v1/v v1)
    (aset-float verts vert-x2/v x1)
    (aset-float verts vert-y2/v y2)
    (aset-float verts vert-c2/v color12)
    (aset-float verts vert-u2/v u1)
    (aset-float verts vert-v2/v v2)
    (aset-float verts vert-x3/v x2)
    (aset-float verts vert-y3/v y2)
    (aset-float verts vert-c3/v color22)
    (aset-float verts vert-u3/v u2)
    (aset-float verts vert-v3/v v2)
    (aset-float verts vert-x4/v x2)
    (aset-float verts vert-y4/v y1)
    (aset-float verts vert-c4/v color21)
    (aset-float verts vert-u4/v u2)
    (aset-float verts vert-v4/v v1)
    (draw/f batch
          (texture-region/get-texture region)
          verts
          0
          num-vertices)))
