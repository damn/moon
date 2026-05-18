(ns com.badlogic.gdx.maps.renderer
  (:require [clojure.tiled-map :as tiled-map]
            [clojure.tiled-map.layer :as layer]
            [clojure.tiled-map.layers :as layers])
  (:import (clojure.gdx TiledMapRenderer
                        TiledMapRenderer$ColorSetter)))

(defn draw! [batch world-unit-scale camera tiled-map color-setter]
  (let [renderer (doto (TiledMapRenderer. tiled-map (float world-unit-scale) batch)
                   (.setColorSetter (reify TiledMapRenderer$ColorSetter
                                      (apply [_ color x y]
                                        (color-setter color x y))))
                   (.setView camera))
        layers (tiled-map/layers tiled-map)]
    (->> layers
         (filter layer/visible?)
         (map #(layers/get-index layers %))
         int-array
         (.render renderer))))
