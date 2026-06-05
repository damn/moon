(ns clojure.batch.draw-tiled-map
  (:require [clojure.maps.tiled.tiled-map.get-layers :refer [get-layers]])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.graphics.g2d Batch)
           (com.badlogic.gdx.maps.tiled TiledMapTileLayer)
           (com.badlogic.gdx.math Rectangle)
           (tiled TiledMapRenderer
                  TiledMapRenderer$ColorSetter)))

(defn draw-tiled-map!
  [batch world-unit-scale camera tiled-map color-setter]
  (Batch/.setProjectionMatrix batch (.combined ^OrthographicCamera camera))
  (Batch/.begin batch)
  (let [viewBounds (Rectangle.)]
    (TiledMapRenderer/render tiled-map
                             (float world-unit-scale)
                             viewBounds
                             camera
                             batch
                             (reify TiledMapRenderer$ColorSetter
                               (apply [_ color x y]
                                 (color-setter color x y)))
                             (let [layers (get-layers tiled-map)]
                               (->> layers
                                    (filter TiledMapTileLayer/.isVisible)
                                    (map #(.getIndex layers ^TiledMapTileLayer %))
                                    int-array))))
  (Batch/.end batch))
