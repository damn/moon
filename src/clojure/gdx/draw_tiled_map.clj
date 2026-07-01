(ns clojure.gdx.draw-tiled-map
  (:require [clojure.gdx.draw-tiled-map-tile-layer :as draw-tiled-map-tile-layer]
            [clojure.gdx.orthographic-camera.combined :as combined]
            [clojure.gdx.orthographic-camera.position :as position]
            [clojure.gdx.orthographic-camera.up :as up]
            [clojure.gdx.orthographic-camera.viewport-height :as viewport-height]
            [clojure.gdx.orthographic-camera.viewport-width :as viewport-width]
            [clojure.gdx.orthographic-camera.zoom :as zoom])
  (:import (com.badlogic.gdx.graphics.g2d Batch)
           (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer)))

(defn f!
  [^Batch batch
   world-unit-scale
   camera
   ^TiledMap tiled-map
   color-setter]
  (.setProjectionMatrix batch (combined/f camera))
  (.begin batch)
  (let [width  (* (viewport-width/f camera) (zoom/f camera))
        height (* (viewport-height/f camera) (zoom/f camera))
        up (up/f camera)
        w (+ (* width  (Math/abs (.y up)))
             (* height (Math/abs (.x up))))
        h (+ (* height (Math/abs (.y up)))
             (* width  (Math/abs (.x up))))
        pos (position/f camera)
        viewBounds {:x (- (.x pos) (/ w 2))
                    :y (- (.y pos) (/ h 2))
                    :width w
                    :height h}]
    (doseq [^TiledMapTileLayer layer (filter TiledMapTileLayer/.isVisible (.getLayers tiled-map))]
      (draw-tiled-map-tile-layer/f! layer
                                    batch
                                    (float world-unit-scale)
                                    viewBounds
                                    color-setter)))
  (.end batch))
