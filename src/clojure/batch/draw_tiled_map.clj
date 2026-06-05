(ns clojure.batch.draw-tiled-map
  (:require [clojure.maps.tiled.tiled-map.get-layers :refer [get-layers]])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)
           (tiled TiledMapRenderer
                  TiledMapRenderer$ColorSetter)))

(defn draw-tiled-map!
  [batch world-unit-scale camera tiled-map color-setter]
  (TiledMapRenderer/render tiled-map
                           (float world-unit-scale)
                           camera
                           batch
                           (reify TiledMapRenderer$ColorSetter
                             (apply [_ color x y]
                               (color-setter color x y)))
                           (let [layers (get-layers tiled-map)]
                             (->> layers
                                  (filter TiledMapTileLayer/.isVisible) ; TODO already done
                                  (map #(.getIndex layers ^TiledMapTileLayer %))
                                  int-array))))
