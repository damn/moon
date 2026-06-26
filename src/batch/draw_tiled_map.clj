(ns batch.draw-tiled-map
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as camera]
            [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [tiled-map.get-layers :refer [get-layers]]
            [batch.render-tile-layer :refer [render-tile-layer!]]))

(defn draw-tiled-map!
  [batch
   world-unit-scale
   camera
   tiled-map
   color-setter]
  (batch/set-projection-matrix! batch (camera/combined camera))
  (batch/begin! batch)
  (let [width  (* (camera/viewport-width  camera) (camera/zoom camera))
        height (* (camera/viewport-height camera) (camera/zoom camera))
        w (+ (* width  (Math/abs (.y (camera/up camera))))
             (* height (Math/abs (.x (camera/up camera)))))
        h (+ (* height (Math/abs (.y (camera/up camera))))
             (* width  (Math/abs (.x (camera/up camera)))))
        viewBounds {:x (- (.x (camera/position camera)) (/ w 2))
                    :y (- (.y (camera/position camera)) (/ h 2))
                    :width w
                    :height h}]
    (doseq [layer (filter layer/visible? (get-layers tiled-map))]
      (render-tile-layer! layer
                          batch
                          (float world-unit-scale)
                          viewBounds
                          color-setter)))
  (batch/end! batch))
