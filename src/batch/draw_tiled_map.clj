(ns batch.draw-tiled-map
  (:require [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [tiled-map.get-layers :refer [get-layers]]
            [batch.render-tile-layer :refer [render-tile-layer!]])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.maps.tiled TiledMapTileLayer)
           (com.badlogic.gdx.math Rectangle)))

(defn draw-tiled-map!
  [batch
   world-unit-scale
   ^OrthographicCamera camera
   tiled-map
   color-setter]
  (batch/set-projection-matrix! batch (.combined camera))
  (batch/begin! batch)
  (let [width  (* (.viewportWidth  camera) (.zoom camera))
        height (* (.viewportHeight camera) (.zoom camera))
        w (+ (* width  (Math/abs (.y (.up camera))))
             (* height (Math/abs (.x (.up camera)))))
        h (+ (* height (Math/abs (.y (.up camera))))
             (* width  (Math/abs (.x (.up camera)))))
        viewBounds (Rectangle. (- (.x (.position camera)) (/ w 2))
                               (- (.y (.position camera)) (/ h 2))
                               w
                               h)]
    (doseq [layer (filter TiledMapTileLayer/.isVisible (get-layers tiled-map))]
      (render-tile-layer! layer
                          batch
                          (float world-unit-scale)
                          viewBounds
                          color-setter)))
  (batch/end! batch))
