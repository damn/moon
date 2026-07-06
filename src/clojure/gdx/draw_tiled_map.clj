(ns clojure.gdx.draw-tiled-map
  (:require
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [clojure.gdx.draw-tiled-map-tile-layer :as draw-tiled-map-tile-layer]
            [clojure.gdx.tiled-map.get-layers :as get-layers]
            [clojure.gdx.tiled-map-tile-layer.visible? :as visible?]
            [com.badlogic.gdx.math.vector3 :as vector3]))

(defn f!
  [batch
   world-unit-scale
   camera
   tiled-map
   color-setter]
  (batch/set-projection-matrix! batch (orthographic-camera/combined camera))
  (batch/begin! batch)
  (let [width  (* (orthographic-camera/viewport-width camera) (orthographic-camera/zoom camera))
        height (* (orthographic-camera/viewport-height camera) (orthographic-camera/zoom camera))
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
    (doseq [layer (filter visible?/f (get-layers/f tiled-map))]
      (draw-tiled-map-tile-layer/f! layer
                                    batch
                                    world-unit-scale
                                    viewBounds
                                    color-setter)))
  (batch/end! batch))
