(ns moon.create.grid
  (:require [moon.grid :as grid]
            [moon.tiled-map :as tiled-map])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn step [{:keys [^TiledMap ctx/tiled-map] :as ctx}]
  (assoc ctx :ctx/grid (grid/create (.get (.getProperties tiled-map) "width")
                                    (.get (.getProperties tiled-map) "height")
                                    #(case (tiled-map/movement-property tiled-map %)
                                       "none" :none
                                       "air"  :air
                                       "all"  :all))))
