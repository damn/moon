(ns gdx.graphics.g2d.batch.draw-tiled-map
  (:require [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [com.badlogic.gdx.math.vector3 :as vector3]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [gdx.maps.tiled.draw-tile-layer :as draw-tile-layer]))

(defn draw!
  [batch
   world-unit-scale
   camera
   tiled-map
   color-setter]
  (batch/setProjectionMatrix batch (orthographic-camera/combined camera))
  (batch/begin batch)
  (let [width  (* (orthographic-camera/viewportWidth camera) (orthographic-camera/zoom camera))
        height (* (orthographic-camera/viewportHeight camera) (orthographic-camera/zoom camera))
        up (orthographic-camera/up camera)
        w (+ (* width  (Math/abs (float (vector3/y up))))
             (* height (Math/abs (float (vector3/x up)))))
        h (+ (* height (Math/abs (float (vector3/y up))))
             (* width  (Math/abs (float (vector3/x up)))))
        pos (orthographic-camera/position camera)
        viewBounds {:x (- (vector3/x pos) (/ w 2))
                    :y (- (vector3/y pos) (/ h 2))
                    :width w
                    :height h}]
    (doseq [layer (filter tiled-map-tile-layer/isVisible (tiled-map/getLayers tiled-map))]
      (draw-tile-layer/draw! layer
                             batch
                             world-unit-scale
                             viewBounds
                             color-setter)))
  (batch/end batch))
