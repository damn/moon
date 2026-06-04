(ns clojure.gdx.maps.tiled.tiled-map
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn create []
  (TiledMap.))

(defn layers [^TiledMap tiled-map]
  (.getLayers tiled-map))
