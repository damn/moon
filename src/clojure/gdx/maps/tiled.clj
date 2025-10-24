(ns clojure.gdx.maps.tiled
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn create []
  (TiledMap.))

(defn properties [^TiledMap tiled-map]
  (.getProperties tiled-map))

(defn layers [^TiledMap tiled-map]
  (.getLayers tiled-map))

(defn dispose! [^TiledMap tiled-map]
  (.dispose tiled-map))
