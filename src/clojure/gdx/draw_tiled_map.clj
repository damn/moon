(ns clojure.gdx.draw-tiled-map
  (:require [clojure.gdx.batch.begin! :as begin!]
            [clojure.gdx.batch.end! :as end!]
            [clojure.gdx.batch.set-projection-matrix! :as set-projection-matrix!]
            [clojure.gdx.draw-tiled-map-tile-layer :as draw-tiled-map-tile-layer]
            [clojure.gdx.orthographic-camera.combined :as combined]
            [clojure.gdx.orthographic-camera.position :as position]
            [clojure.gdx.orthographic-camera.up :as up]
            [clojure.gdx.orthographic-camera.viewport-height :as viewport-height]
            [clojure.gdx.orthographic-camera.viewport-width :as viewport-width]
            [clojure.gdx.orthographic-camera.zoom :as zoom]
            [clojure.gdx.tiled-map.get-layers :as get-layers]
            [clojure.gdx.tiled-map-tile-layer.visible? :as visible?]))

(defn f!
  [batch
   world-unit-scale
   camera
   tiled-map
   color-setter]
  (set-projection-matrix!/f batch (combined/f camera))
  (begin!/f batch)
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
    (doseq [layer (filter visible?/f (get-layers/f tiled-map))]
      (draw-tiled-map-tile-layer/f! layer
                                    batch
                                    (float world-unit-scale)
                                    viewBounds
                                    color-setter)))
  (end!/f batch))
