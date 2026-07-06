(ns clojure.gdx.draw-tiled-map
  (:require
            [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [clojure.gdx.draw-tiled-map-tile-layer :as draw-tiled-map-tile-layer]
            [clojure.gdx.orthographic-camera.combined :as combined]
            [clojure.gdx.orthographic-camera.position :as position]
            [clojure.gdx.orthographic-camera.up :as up]
            [clojure.gdx.orthographic-camera.viewport-height :as viewport-height]
            [clojure.gdx.orthographic-camera.viewport-width :as viewport-width]
            [clojure.gdx.orthographic-camera.zoom :as zoom]
            [clojure.gdx.tiled-map.get-layers :as get-layers]
            [clojure.gdx.tiled-map-tile-layer.visible? :as visible?]
            [com.badlogic.gdx.math.vector3 :as vector3]))

(defn f!
  [batch
   world-unit-scale
   camera
   tiled-map
   color-setter]
  (batch/set-projection-matrix! batch (combined/f camera))
  (batch/begin! batch)
  (let [width  (* (viewport-width/f camera) (zoom/f camera))
        height (* (viewport-height/f camera) (zoom/f camera))
        up (up/f camera)
        w (+ (* width  (Math/abs (float (vector3/y up))))
             (* height (Math/abs (float (vector3/x up)))))
        h (+ (* height (Math/abs (float (vector3/y up))))
             (* width  (Math/abs (float (vector3/x up)))))
        pos (position/f camera)
        viewBounds {:x (- (vector3/x pos) (/ w 2))
                    :y (- (vector3/y pos) (/ h 2))
                    :width w
                    :height h}]
    (doseq [layer (filter visible?/f (get-layers/f tiled-map))]
      (draw-tiled-map-tile-layer/f! layer
                                    batch
                                    world-unit-scale
                                    viewBounds
                                    color-setter)))
  (batch/end! batch))
