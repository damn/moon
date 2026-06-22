(ns gdl.tiled-map.get-layers
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn get-layers [^TiledMap tiled-map]
  (.getLayers tiled-map))
