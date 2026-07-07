(ns clojure.tmx-map-loader
  (:refer-clojure :exclude [new load])
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn new []
  (TmxMapLoader.))

(defn load! [^TmxMapLoader tmx-map-loader path]
  (.load tmx-map-loader path))
