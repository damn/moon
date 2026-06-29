(ns batch.draw-tiled-map
  (:require [batch.render-tile-layer :refer [render-tile-layer!]])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.graphics.g2d Batch)
           (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer)))

(defn draw-tiled-map!
  [^Batch batch
   world-unit-scale
   ^OrthographicCamera camera
   ^TiledMap tiled-map
   color-setter]
  (.setProjectionMatrix batch (.combined camera))
  (.begin batch)
  (let [width  (* (.viewportWidth  camera) (.zoom camera))
        height (* (.viewportHeight camera) (.zoom camera))
        w (+ (* width  (Math/abs (.y (.up camera))))
             (* height (Math/abs (.x (.up camera)))))
        h (+ (* height (Math/abs (.y (.up camera))))
             (* width  (Math/abs (.x (.up camera)))))
        viewBounds {:x (- (.x (.position camera)) (/ w 2))
                    :y (- (.y (.position camera)) (/ h 2))
                    :width w
                    :height h}]
    (doseq [^TiledMapTileLayer layer (filter TiledMapTileLayer/.isVisible (.getLayers tiled-map))]
      (render-tile-layer! layer
                          batch
                          (float world-unit-scale)
                          viewBounds
                          color-setter)))
  (.end batch))
