(ns clojure.gdx.tiled-map-tile-layer.get-width
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn f [^TiledMapTileLayer layer]
  (.getWidth layer))
