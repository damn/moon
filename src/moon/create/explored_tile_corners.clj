(ns moon.create.explored-tile-corners
  (:require [moon.grid2d :as g2d])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn step [{:keys [^TiledMap ctx/tiled-map] :as ctx}]
  (assoc ctx :ctx/explored-tile-corners (atom (g2d/create-grid (.get (.getProperties tiled-map) "width")
                                                               (.get (.getProperties tiled-map) "height")
                                                               (constantly false)))))
