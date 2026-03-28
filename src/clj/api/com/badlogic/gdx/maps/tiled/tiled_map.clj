(ns clj.api.com.badlogic.gdx.maps.tiled.tiled-map
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn properties [^TiledMap tiled-map]
  (.getProperties tiled-map))
