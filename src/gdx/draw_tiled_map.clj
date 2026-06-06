(ns gdx.draw-tiled-map
  (:require [gdx.maps.tiled.tiled-map.get-layers :refer [get-layers]])
  (:import (com.badlogic.gdx.graphics Color
                                      OrthographicCamera)
           (com.badlogic.gdx.graphics.g2d Batch
                                          TextureRegion)
           (com.badlogic.gdx.maps.tiled TiledMapTile
                                        TiledMapTileLayer
                                        TiledMapTileLayer$Cell)
           (com.badlogic.gdx.math Rectangle)))

(def ^:private num-vertices 20)

(def ^:private vertices (float-array num-vertices))

(defn- render-tile-layer
  [^TiledMapTileLayer layer
   ^Batch batch
   unit-scale
   ^Rectangle view-bounds
   color-setter]

  (let [^Color batch-color (.getColor batch)

        layer-width (.getWidth layer)
        layer-height (.getHeight layer)

        layer-tile-width (* (.getTileWidth layer) unit-scale)
        layer-tile-height (* (.getTileHeight layer) unit-scale)

        layer-offset-x (* (.getRenderOffsetX layer) unit-scale)
        ; offset in tiled is y down, so we flip it
        layer-offset-y (* (- (.getRenderOffsetY layer)) unit-scale)

        col1 (max 0
                  (int (/ (- (.x view-bounds) layer-offset-x)
                          layer-tile-width)))

        col2 (min layer-width
                  (int (/ (+ (.x view-bounds)
                             (.width view-bounds)
                             layer-tile-width
                             (- layer-offset-x))
                          layer-tile-width)))

        row1 (max 0
                  (int (/ (- (.y view-bounds) layer-offset-y)
                          layer-tile-height)))

        row2 (min layer-height
                  (int (/ (+ (.y view-bounds)
                             (.height view-bounds)
                             layer-tile-height
                             (- layer-offset-y))
                          layer-tile-height)))

        x-start (+ (* col1 layer-tile-width)
                   layer-offset-x)

        ^floats verts (aclone ^floats vertices)]

    (loop [row row2
           y (+ (* row2 layer-tile-height)
                layer-offset-y)]

      (when (>= row row1)

        (loop [col col1
               x x-start]

          (when (< col col2)

            (when-let [^TiledMapTileLayer$Cell cell
                       (.getCell layer col row)]

              (when-let [^TiledMapTile tile
                         (.getTile cell)]

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

                  ;; vertex data
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

                  ;; flip x
                  (when flip-x
                    (let [tmp (aget verts Batch/U1)]
                      (aset-float verts Batch/U1 (aget verts Batch/U3))
                      (aset-float verts Batch/U3 tmp))
                    (let [tmp (aget verts Batch/U2)]
                      (aset-float verts Batch/U2 (aget verts Batch/U4))
                      (aset-float verts Batch/U4 tmp)))

                  ;; flip y
                  (when flip-y
                    (let [tmp (aget verts Batch/V1)]
                      (aset-float verts Batch/V1 (aget verts Batch/V3))
                      (aset-float verts Batch/V3 tmp))
                    (let [tmp (aget verts Batch/V2)]
                      (aset-float verts Batch/V2 (aget verts Batch/V4))
                      (aset-float verts Batch/V4 tmp)))

                  ;; rotations
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
                         (int num-vertices)))))

            (recur (inc col)
                   (+ x layer-tile-width))))

        (recur (dec row)
               (- y layer-tile-height))))))

(defn draw-tiled-map!
  [^Batch batch
   world-unit-scale
   ^OrthographicCamera camera
   tiled-map
   color-setter]
  (.setProjectionMatrix batch (.combined camera))
  (.begin batch)
  (let [width  (* (.viewportWidth  camera) (.zoom camera))
        height (* (.viewportHeight camera) (.zoom camera))
        w (+ (* width  (Math/abs (.y (.up camera))))
             (* height (Math/abs (.x (.up camera)))))
        h (+ (* height (Math/abs (.y (.up camera))))
             (* width  (Math/abs (.x (.up camera)))))
        viewBounds (Rectangle. (- (.x (.position camera)) (/ w 2))
                               (- (.y (.position camera)) (/ h 2))
                               w
                               h)]
    (doseq [layer (filter TiledMapTileLayer/.isVisible (get-layers tiled-map))]
      (render-tile-layer layer
                         batch
                         (float world-unit-scale)
                         viewBounds
                         color-setter)))
  (.end batch))
