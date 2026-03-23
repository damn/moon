(ns moon.create.content-grid
  (:require [moon.content-grid :as content-grid])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn step
  [{:keys [^TiledMap ctx/tiled-map]
    :as ctx}
   cell-size]
  (assoc ctx :ctx/content-grid (content-grid/create (.get (.getProperties tiled-map) "width")
                                                    (.get (.getProperties tiled-map) "height")
                                                    cell-size)))

; TODO content-grid protocol
