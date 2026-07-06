(ns clojure.gdx.draw-tiled-map-tile-layer
  (:require
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [clojure.gdx.tiled-map-tile-layer$cell.get-tile :as get-tile]
            [clojure.gdx.draw-tiled-map-tile :as draw-tile])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn f!
  [^TiledMapTileLayer layer
   batch
   unit-scale
   view-bounds
   color-setter]
  (let [num-vertices 20
        vertices (float-array num-vertices)
        batch-color (batch/get-color batch)
        layer-width (.getWidth layer)
        layer-height (.getHeight layer)
        layer-tile-width (* (.getTileWidth layer) unit-scale)
        layer-tile-height (* (.getTileHeight layer) unit-scale)
        layer-offset-x (* (.getRenderOffsetX layer) unit-scale)
        ; offset in tiled is y down, so we flip it
        layer-offset-y (* (- (.getRenderOffsetY layer)) unit-scale)
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
            (when-let [cell (.getCell layer col row)]
              (when-let [tile (get-tile/f cell)]
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
