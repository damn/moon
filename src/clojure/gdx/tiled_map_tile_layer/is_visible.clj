(ns clojure.gdx.tiled-map-tile-layer.is-visible
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn f [^TiledMapTileLayer layer]
  (.isVisible layer))
