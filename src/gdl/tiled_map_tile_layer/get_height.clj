(ns gdl.tiled-map-tile-layer.get-height
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn get-height [^TiledMapTileLayer layer]
  (.getHeight layer))
