(ns clojure.gdx.tmx-map-loader.load
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn f [^TmxMapLoader tmx-map-loader path]
  (.load tmx-map-loader path))
