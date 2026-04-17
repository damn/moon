(ns clojure.gdx.tiled-map.tile
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile)))

(defn properties [^TiledMapTile tile]
  (.getProperties tile))
