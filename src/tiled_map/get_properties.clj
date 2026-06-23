(ns tiled-map.get-properties
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn f [^TiledMap tiled-map]
  (.getProperties tiled-map))
