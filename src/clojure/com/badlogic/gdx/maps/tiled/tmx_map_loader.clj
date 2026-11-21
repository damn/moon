(ns clojure.com.badlogic.gdx.maps.tiled.tmx-map-loader
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn create []
  (TmxMapLoader.))

(defn load [^TmxMapLoader loader path]
  (.load loader path))
