(ns clojure.batch.draw-tiled-map
  (:require [clojure.maps.tiled.tiled-map.get-layers :refer [get-layers]])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.graphics.g2d Batch)
           (com.badlogic.gdx.maps.tiled TiledMapTileLayer)
           (com.badlogic.gdx.math Rectangle)
           (tiled TiledMapRenderer
                  TiledMapRenderer$ColorSetter)))

(defn draw-tiled-map!
  [^Batch batch
   world-unit-scale
   ^OrthographicCamera camera
   tiled-map
   color-setter]
  (.setProjectionMatrix batch (.combined camera))
  (.begin batch)
  (let [viewBounds (Rectangle.)
        width  (* (.viewportWidth  camera) (.zoom camera))
        height (* (.viewportHeight camera) (.zoom camera))
        w (+ (* width  (Math/abs (.y (.up camera))))
             (* height (Math/abs (.x (.up camera)))))
        h (+ (* height (Math/abs (.y (.up camera))))
             (* width  (Math/abs (.x (.up camera)))))]
    (.set viewBounds
          (- (.x (.position camera)) (/ w 2))
          (- (.y (.position camera)) (/ h 2))
          w
          h)
    (doseq [layer (filter TiledMapTileLayer/.isVisible (get-layers tiled-map))]
      (TiledMapRenderer/renderMapLayer layer
                                       batch
                                       (float world-unit-scale)
                                       viewBounds
                                       (reify TiledMapRenderer$ColorSetter
                                         (apply [_ color x y]
                                           (color-setter color x y))))))
  (.end batch))
