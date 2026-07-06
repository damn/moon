(ns clojure.gdx.draw-tiled-map-tile
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.graphics.g2d.batch :as batch]))

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
    (aset-float verts batch/x1 x1)
    (aset-float verts batch/y1 y1)
    (aset-float verts batch/c1 color11)
    (aset-float verts batch/u1 u1)
    (aset-float verts batch/v1 v1)
    (aset-float verts batch/x2 x1)
    (aset-float verts batch/y2 y2)
    (aset-float verts batch/c2 color12)
    (aset-float verts batch/u2 u1)
    (aset-float verts batch/v2 v2)
    (aset-float verts batch/x3 x2)
    (aset-float verts batch/y3 y2)
    (aset-float verts batch/c3 color22)
    (aset-float verts batch/u3 u2)
    (aset-float verts batch/v3 v2)
    (aset-float verts batch/x4 x2)
    (aset-float verts batch/y4 y1)
    (aset-float verts batch/c4 color21)
    (aset-float verts batch/u4 u2)
    (aset-float verts batch/v4 v1)
    (batch/draw! batch
             (texture-region/get-texture region)
             verts
             0
             num-vertices)))
