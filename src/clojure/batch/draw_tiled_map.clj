(ns clojure.batch.draw-tiled-map
  (:import (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer)
           (tiled TiledMapRenderer
                  TiledMapRenderer$ColorSetter)))

(defn draw-tiled-map!
  [batch world-unit-scale camera tiled-map color-setter]
  (let [renderer (TiledMapRenderer. tiled-map
                                    (float world-unit-scale)
                                    camera
                                    batch
                                    (reify TiledMapRenderer$ColorSetter
                                      (apply [_ color x y]
                                        (color-setter color x y))))
        layers (TiledMap/.getLayers tiled-map)]
    (->> layers
         (filter TiledMapTileLayer/.isVisible) ; TODO already done
         (map #(.getIndex layers ^TiledMapTileLayer %))
         int-array
         (.render renderer))))
