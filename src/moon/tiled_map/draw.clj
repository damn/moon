(ns moon.tiled-map.draw
  (:import (com.badlogic.gdx.graphics OrthographicCamera
                                      Texture)
           (com.badlogic.gdx.graphics.g2d Batch
                                          TextureRegion)
           (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTile
                                        TiledMapTileLayer
                                        TiledMapTileLayer$Cell)))

(defn- draw-tile!
  [x
   y
   tile
   unit-scale
   color-setter
   batch-color
   verts
   batch
   num-vertices]
  (let [region (.getTextureRegion ^TiledMapTile tile)
        x1 (+ x (* (.getOffsetX ^TiledMapTile tile) unit-scale))
        y1 (+ y (* (.getOffsetY ^TiledMapTile tile) unit-scale))
        x2 (+ x1 (* (.getRegionWidth ^TextureRegion region) unit-scale))
        y2 (+ y1 (* (.getRegionHeight ^TextureRegion region) unit-scale))
        u1 (.getU ^TextureRegion region)
        v1 (.getV2 ^TextureRegion region)
        u2 (.getU2 ^TextureRegion region)
        v2 (.getV ^TextureRegion region)
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
    (.draw ^Batch batch
           ^Texture (.getTexture ^TextureRegion region)
           ^floats verts
           (int 0)
           (int num-vertices))))

(defn- draw-tile-layer!
  [layer
   batch
   unit-scale
   view-bounds
   color-setter]
  (let [num-vertices 20
        vertices (float-array num-vertices)
        batch-color (.getColor ^Batch batch)
        layer-width (.getWidth ^TiledMapTileLayer layer)
        layer-height (.getHeight ^TiledMapTileLayer layer)
        layer-tile-width (* (.getTileWidth ^TiledMapTileLayer layer) unit-scale)
        layer-tile-height (* (.getTileHeight ^TiledMapTileLayer layer) unit-scale)
        layer-offset-x (* (.getRenderOffsetX ^TiledMapTileLayer layer) unit-scale)
        layer-offset-y (* (- (.getRenderOffsetY ^TiledMapTileLayer layer)) unit-scale)
        col1 (max 0
                  (int (/ (- (:x view-bounds) layer-offset-x)
                          layer-tile-width)))
        col2 (min layer-width
                  (int (/ (+ (:x view-bounds)
                             (:width view-bounds)
                             layer-tile-width
                             (- layer-offset-x))
                          layer-tile-width)))
        row1 (max 0
                  (int (/ (- (:y view-bounds) layer-offset-y)
                          layer-tile-height)))
        row2 (min layer-height
                  (int (/ (+ (:y view-bounds)
                             (:height view-bounds)
                             layer-tile-height
                             (- layer-offset-y))
                          layer-tile-height)))
        x-start (+ (* col1 layer-tile-width)
                   layer-offset-x)
        verts (aclone vertices)]
    (loop [row row2
           y (+ (* row2 layer-tile-height)
                layer-offset-y)]
      (when (>= row row1)
        (loop [col col1
               x x-start]
          (when (< col col2)
            (when-let [cell (.getCell ^TiledMapTileLayer layer (int col) (int row))]
              (when-let [tile (.getTile ^TiledMapTileLayer$Cell cell)]
                (draw-tile! x
                            y
                            tile
                            unit-scale
                            color-setter
                            batch-color
                            verts
                            batch
                            num-vertices)))
            (recur (inc col)
                   (+ x layer-tile-width))))
        (recur (dec row)
               (- y layer-tile-height))))))

(defn draw!
  [^TiledMap tiled-map
   ^Batch batch
   world-unit-scale
   ^OrthographicCamera camera
   color-setter]
  (let [width  (* (.viewportWidth camera) (.zoom camera))
        height (* (.viewportHeight camera) (.zoom camera))
        up (.up camera)
        w (+ (* width  (Math/abs (float (.y up))))
             (* height (Math/abs (float (.x up)))))
        h (+ (* height (Math/abs (float (.y up))))
             (* width  (Math/abs (float (.x up)))))
        pos (.position camera)
        view-bounds {:x (- (.x pos) (/ w 2))
                     :y (- (.y pos) (/ h 2))
                     :width w
                     :height h}]
    (doseq [layer (filter #(.isVisible ^TiledMapTileLayer %) (.getLayers tiled-map))]
      (draw-tile-layer! layer
                        batch
                        world-unit-scale
                        view-bounds
                        color-setter))))
