(ns clojure.gdx.draw-tiled-map-tile
  (:require [clojure.gdx.batch.draw! :as draw!]
            [clojure.gdx.texture-region.get-region-height :as get-region-height]
            [clojure.gdx.texture-region.get-region-width :as get-region-width]
            [clojure.gdx.texture-region.get-texture :as get-texture]
            [clojure.gdx.texture-region.get-u :as get-u]
            [clojure.gdx.texture-region.get-u2 :as get-u2]
            [clojure.gdx.texture-region.get-v :as get-v]
            [clojure.gdx.texture-region.get-v2 :as get-v2]
            [clojure.gdx.tiled-map-tile.get-texture-region :as get-texture-region]
            [clojure.gdx.tiled-map-tile.get-offset-x :as get-offset-x]
            [clojure.gdx.tiled-map-tile.get-offset-y :as get-offset-y])
  (:import (com.badlogic.gdx.graphics.g2d Batch)))

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
  (let [region (get-texture-region/f tile)
        x1 (+ x (* (get-offset-x/f tile) unit-scale))
        y1 (+ y (* (get-offset-y/f tile) unit-scale))
        x2 (+ x1 (* (get-region-width/f region) unit-scale))
        y2 (+ y1 (* (get-region-height/f region) unit-scale))
        u1 (get-u/f region)
        v1 (get-v2/f region)
        u2 (get-u2/f region)
        v2 (get-v/f region)
        color11 (float (color-setter batch-color x1 y1))
        color12 (float (color-setter batch-color x1 y2))
        color22 (float (color-setter batch-color x2 y2))
        color21 (float (color-setter batch-color x2 y1))]
    (aset-float verts Batch/X1 x1)
    (aset-float verts Batch/Y1 y1)
    (aset-float verts Batch/C1 color11)
    (aset-float verts Batch/U1 u1)
    (aset-float verts Batch/V1 v1)
    (aset-float verts Batch/X2 x1)
    (aset-float verts Batch/Y2 y2)
    (aset-float verts Batch/C2 color12)
    (aset-float verts Batch/U2 u1)
    (aset-float verts Batch/V2 v2)
    (aset-float verts Batch/X3 x2)
    (aset-float verts Batch/Y3 y2)
    (aset-float verts Batch/C3 color22)
    (aset-float verts Batch/U3 u2)
    (aset-float verts Batch/V3 v2)
    (aset-float verts Batch/X4 x2)
    (aset-float verts Batch/Y4 y1)
    (aset-float verts Batch/C4 color21)
    (aset-float verts Batch/U4 u2)
    (aset-float verts Batch/V4 v1)
    (draw!/f batch
             (get-texture/f region)
             verts
             0
             num-vertices)))
