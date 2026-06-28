(ns tiled-map-tile-layer.get-width
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn get-width [^TiledMapTileLayer layer]
  (.getWidth layer))
