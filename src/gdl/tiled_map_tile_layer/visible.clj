(ns gdl.tiled-map-tile-layer.visible
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn visible? [^TiledMapTileLayer layer]
  (.isVisible layer))
