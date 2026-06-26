(ns batch.render-tile-layer
  (:require [batch.draw-tile :as draw-tile]
            [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer-cell :as cell]))

(defn render-tile-layer!
  [layer
   batch
   unit-scale
   view-bounds
   color-setter]
  (let [num-vertices 20
        vertices (float-array num-vertices)
        batch-color (batch/color batch)
        layer-width (layer/width layer)
        layer-height (layer/height layer)
        layer-tile-width (* (layer/tile-width layer) unit-scale)
        layer-tile-height (* (layer/tile-height layer) unit-scale)
        layer-offset-x (* (layer/render-offset-x layer) unit-scale)
        ; offset in tiled is y down, so we flip it
        layer-offset-y (* (- (layer/render-offset-y layer)) unit-scale)
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
            (when-let [cell (layer/cell layer col row)]
              (when-let [tile (cell/tile cell)]
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
