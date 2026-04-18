(ns clojure.gdx.maps.tiled.tile
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile)))

(defn properties [^TiledMapTile tile]
  (.getProperties tile))
