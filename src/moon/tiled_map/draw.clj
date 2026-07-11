(ns moon.tiled-map.draw
  (:require [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer$cell :as tiled-map-tile-layer-cell]
            [gdx.math.vector3 :as vector3]
            [moon.orthographic-camera :as orthographic-camera]))

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

(defn- draw-tile-layer!
  [layer
   batch
   unit-scale
   view-bounds
   color-setter]
  (let [num-vertices 20
        vertices (float-array num-vertices)
        batch-color (batch/getColor batch)
        layer-width (tiled-map-tile-layer/getWidth layer)
        layer-height (tiled-map-tile-layer/getHeight layer)
        layer-tile-width (* (tiled-map-tile-layer/getTileWidth layer) unit-scale)
        layer-tile-height (* (tiled-map-tile-layer/getTileHeight layer) unit-scale)
        layer-offset-x (* (tiled-map-tile-layer/getRenderOffsetX layer) unit-scale)
        layer-offset-y (* (- (tiled-map-tile-layer/getRenderOffsetY layer)) unit-scale)
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
            (when-let [cell (tiled-map-tile-layer/getCell layer col row)]
              (when-let [tile (tiled-map-tile-layer-cell/getTile cell)]
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
  [batch
   world-unit-scale
   camera
   tiled-map
   color-setter]
  (batch/setProjectionMatrix batch (orthographic-camera/combined camera))
  (batch/begin batch)
  (let [width  (* (orthographic-camera/viewport-width camera) (orthographic-camera/zoom camera))
        height (* (orthographic-camera/viewport-height camera) (orthographic-camera/zoom camera))
        up (orthographic-camera/up camera)
        w (+ (* width  (Math/abs (float (vector3/y up))))
             (* height (Math/abs (float (vector3/x up)))))
        h (+ (* height (Math/abs (float (vector3/y up))))
             (* width  (Math/abs (float (vector3/x up)))))
        pos (orthographic-camera/position-vec3 camera)
        view-bounds {:x (- (vector3/x pos) (/ w 2))
                     :y (- (vector3/y pos) (/ h 2))
                     :width w
                     :height h}]
    (doseq [layer (filter tiled-map-tile-layer/isVisible (tiled-map/getLayers tiled-map))]
      (draw-tile-layer! layer
                        batch
                        world-unit-scale
                        view-bounds
                        color-setter)))
  (batch/end batch))
