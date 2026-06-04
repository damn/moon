(ns clojure.gdx.maps.tiled.tiled-map.get-properties
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn get-properties [^TiledMap tiled-map]
  (.getProperties tiled-map))
