(ns clojure.gdx.tiled-map-tile-layer.set-visible
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn f [layer visible?]
  (TiledMapTileLayer/.setVisible layer visible?))
