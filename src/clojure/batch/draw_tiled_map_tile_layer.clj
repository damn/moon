(ns clojure.batch.draw-tiled-map-tile-layer
  (:require [clojure.gdx.graphics.g2d.batch :as batch]
            [clojure.tiled-map-tile-layer$cell :as tiled-map-tile-layer-cell]
            [clojure.tiled-map-tile-layer :as tiled-map-tile-layer]
            [clojure.tiled-map-tile :as tiled-map-tile]
            [clojure.tiled-map :as tiled-map]
            [clojure.batch.draw-tiled-map-tile :as draw-tile]))

(defn f!
  [layer
   batch
   unit-scale
   view-bounds
   color-setter]
  (let [num-vertices 20
        vertices (float-array num-vertices)
        batch-color (batch/get-color batch)
        layer-width (tiled-map-tile-layer/get-width layer)
        layer-height (tiled-map-tile-layer/get-height layer)
        layer-tile-width (* (tiled-map-tile-layer/get-tile-width layer) unit-scale)
        layer-tile-height (* (tiled-map-tile-layer/get-tile-height layer) unit-scale)
        layer-offset-x (* (tiled-map-tile-layer/get-render-offset-x layer) unit-scale)
        ; offset in tiled is y down, so we flip it
        layer-offset-y (* (- (tiled-map-tile-layer/get-render-offset-y layer)) unit-scale)
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
            (when-let [cell (tiled-map-tile-layer/get-cell layer col row)]
              (when-let [tile (tiled-map-tile-layer-cell/get-tile cell)]
                (draw-tile/f! x
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
