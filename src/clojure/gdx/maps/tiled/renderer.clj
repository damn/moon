(ns clojure.gdx.maps.tiled.renderer
  (:require [clojure.gdx.maps.tiled.layer :as layer])
  (:import (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer)
           (clojure.gdx TiledMapRenderer
                        TiledMapRenderer$ColorSetter)))

(defn draw! [batch world-unit-scale camera ^TiledMap tiled-map color-setter]
  (let [renderer (doto (TiledMapRenderer. tiled-map (float world-unit-scale) batch)
                   (.setColorSetter (reify TiledMapRenderer$ColorSetter
                                      (apply [_ color x y]
                                        (color-setter color x y))))
                   (.setView camera))
        layers (.getLayers tiled-map)]
    (->> layers
         (filter layer/visible?)
         (map #(.getIndex layers ^TiledMapTileLayer %))
         int-array
         (.render renderer))))
