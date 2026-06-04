(ns clojure.gdx.maps.tiled.tiled-map-tile-layer.get-properties
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn get-properties [^TiledMapTileLayer layer]
  (.getProperties layer))
