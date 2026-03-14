(ns moon.create.grid
  (:require [moon.grid2d :as g2d]
            [moon.cell :as cell]
            [moon.tiled-map :as tiled-map])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn step [{:keys [^TiledMap ctx/tiled-map] :as ctx}]
  (assoc ctx :ctx/grid (g2d/create-grid (.get (.getProperties tiled-map) "width")
                                        (.get (.getProperties tiled-map) "height")
                                        (fn [position]
                                          (atom (cell/create position
                                                             (case (tiled-map/movement-property tiled-map position)
                                                               "none" :none
                                                               "air"  :air
                                                               "all"  :all)))))))
