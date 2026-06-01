; this to 'clojure.gdx.maps.tiled.renderer.orthogonal-tiled-map-render-with-color-setter
; java same name
; and the function is exposed in 'gdx.graphics/batch' ?
; shape-drawer also batch/shape-drawer ??
(ns gdx.tiled-map-renderer
  (:import (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer)
           (gdx TiledMapRenderer
                TiledMapRenderer$ColorSetter)))

(defn draw!
  [batch world-unit-scale camera tiled-map color-setter]
  (let [renderer (doto (TiledMapRenderer. tiled-map (float world-unit-scale) batch)
                   (.setColorSetter (reify TiledMapRenderer$ColorSetter
                                      (apply [_ color x y]
                                        (color-setter color x y))))
                   (.setView camera))
        layers (TiledMap/.getLayers tiled-map)]
    (->> layers
         (filter TiledMapTileLayer/.isVisible) ; TODO already done
         (map #(.getIndex layers ^TiledMapTileLayer %))
         int-array
         (.render renderer))))
