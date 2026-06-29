(ns clojure.gdx.draw-tiled-map-tile
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d Batch)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled TiledMapTile)))

(defn f!
  [x
   y
   ^TiledMapTile tile
   unit-scale
   color-setter
   batch-color
   verts
   batch
   num-vertices]
  (let [^TextureRegion region (.getTextureRegion tile)
        x1 (+ x (* (.getOffsetX tile) unit-scale))
        y1 (+ y (* (.getOffsetY tile) unit-scale))
        x2 (+ x1 (* (.getRegionWidth region) unit-scale))
        y2 (+ y1 (* (.getRegionHeight region) unit-scale))
        u1 (.getU region)
        v1 (.getV2 region)
        u2 (.getU2 region)
        v2 (.getV region)
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
    (Batch/.draw batch
                 ^Texture (.getTexture region)
                 ^floats verts
                 (int 0)
                 (int num-vertices))))
