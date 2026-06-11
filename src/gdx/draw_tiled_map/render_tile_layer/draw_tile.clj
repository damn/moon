(ns gdx.draw-tiled-map.render-tile-layer.draw-tile
  (:import (com.badlogic.gdx.graphics.g2d Batch
                                          TextureRegion)
           (com.badlogic.gdx.maps.tiled TiledMapTile
                                        TiledMapTileLayer$Cell)))

(defn f!
  [x
   y
   ^TiledMapTileLayer$Cell cell
   ^TiledMapTile tile
   unit-scale
   color-setter
   batch-color
   ^floats verts
   ^Batch batch
   num-vertices]
  (let [flip-x (.getFlipHorizontally cell)
        flip-y (.getFlipVertically cell)
        rotations (.getRotation cell)
        ^TextureRegion region (.getTextureRegion tile)
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
    (when flip-x
      (let [tmp (aget verts Batch/U1)]
        (aset-float verts Batch/U1 (aget verts Batch/U3))
        (aset-float verts Batch/U3 tmp))
      (let [tmp (aget verts Batch/U2)]
        (aset-float verts Batch/U2 (aget verts Batch/U4))
        (aset-float verts Batch/U4 tmp)))
    (when flip-y
      (let [tmp (aget verts Batch/V1)]
        (aset-float verts Batch/V1 (aget verts Batch/V3))
        (aset-float verts Batch/V3 tmp))
      (let [tmp (aget verts Batch/V2)]
        (aset-float verts Batch/V2 (aget verts Batch/V4))
        (aset-float verts Batch/V4 tmp)))
    (case rotations
      TiledMapTileLayer$Cell/ROTATE_90
      (do
       (let [tmp (aget verts Batch/V1)]
         (aset-float verts Batch/V1 (aget verts Batch/V2))
         (aset-float verts Batch/V2 (aget verts Batch/V3))
         (aset-float verts Batch/V3 (aget verts Batch/V4))
         (aset-float verts Batch/V4 tmp))

       (let [tmp (aget verts Batch/U1)]
         (aset-float verts Batch/U1 (aget verts Batch/U2))
         (aset-float verts Batch/U2 (aget verts Batch/U3))
         (aset-float verts Batch/U3 (aget verts Batch/U4))
         (aset-float verts Batch/U4 tmp)))
      TiledMapTileLayer$Cell/ROTATE_180
      (do
       (let [tmp (aget verts Batch/U1)]
         (aset-float verts Batch/U1 (aget verts Batch/U3))
         (aset-float verts Batch/U3 tmp))
       (let [tmp (aget verts Batch/U2)]
         (aset-float verts Batch/U2 (aget verts Batch/U4))
         (aset-float verts Batch/U4 tmp))
       (let [tmp (aget verts Batch/V1)]
         (aset-float verts Batch/V1 (aget verts Batch/V3))
         (aset-float verts Batch/V3 tmp))
       (let [tmp (aget verts Batch/V2)]
         (aset-float verts Batch/V2 (aget verts Batch/V4))
         (aset-float verts Batch/V4 tmp)))
      TiledMapTileLayer$Cell/ROTATE_270
      (do
       (let [tmp (aget verts Batch/V1)]
         (aset-float verts Batch/V1 (aget verts Batch/V4))
         (aset-float verts Batch/V4 (aget verts Batch/V3))
         (aset-float verts Batch/V3 (aget verts Batch/V2))
         (aset-float verts Batch/V2 tmp))
       (let [tmp (aget verts Batch/U1)]
         (aset-float verts Batch/U1 (aget verts Batch/U4))
         (aset-float verts Batch/U4 (aget verts Batch/U3))
         (aset-float verts Batch/U3 (aget verts Batch/U2))
         (aset-float verts Batch/U2 tmp)))
      nil)
    (.draw batch
           (.getTexture region)
           ^floats verts
           (int 0)
           (int num-vertices))))
