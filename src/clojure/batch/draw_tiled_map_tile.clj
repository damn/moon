(ns clojure.batch.draw-tiled-map-tile
  (:require [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.graphics.texture :as texture]))

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
  (let [region (tiled-map-tile/getTextureRegion tile)
        x1 (+ x (* (tiled-map-tile/getOffsetX tile) unit-scale))
        y1 (+ y (* (tiled-map-tile/getOffsetY tile) unit-scale))
        x2 (+ x1 (* (texture-region/getRegionWidth region) unit-scale))
        y2 (+ y1 (* (texture-region/getRegionHeight region) unit-scale))
        u1 (texture-region/getU region)
        v1 (texture-region/getV2 region)
        u2 (texture-region/getU2 region)
        v2 (texture-region/getV region)
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
    (batch/draw batch
                          (texture-region/getTexture region)
                          verts
                          0
                          num-vertices)))
